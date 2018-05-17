package com.yq.app;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.yq.commons.constants.BeanProperty.UserStatus;
import com.yq.commons.constants.ErrorCode;
import com.yq.commons.ucpaas.SysConfig;
import com.yq.commons.util.TokenHandler;
import com.yq.model.ClassInf;
import com.yq.model.ResponseContent;
import com.yq.model.School;
import com.yq.model.User;
import com.yq.service.inf.SchoolService;
import com.yq.service.inf.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginInterface extends BaseInterface {
    public static final String AUTH_HEADER_NAME = "X-Auth-Token";
    public static final long TEN_DAYS = 1000 * 60 * 60 * 24 * 10;
    private String token_secret = SysConfig.getInstance().getProperty("token.secret");

    @Autowired
    private UserService userService;

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private MessageDigestPasswordEncoder passwordEncoder;

    private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

    /*3.3.1 POST /api/open/login*/
    @RequestMapping(value = "/api/open/login", method = RequestMethod.POST)
    public void PostOpenLogin(HttpServletRequest req, HttpServletResponse res) {
        ResponseContent content = new ResponseContent();
        try {
            JsonObject params = getJsonElementParams(req).getAsJsonObject();
            JsonElement username = params.get("username");
            JsonElement password = params.get("password");
            if (username == null || password == null) {
                setRequestErrorRes(res, "缺少必要请求参数", ErrorCode.REQUEST_PARAM_MISS);
                return;
            }

            User user = userService.selUserByUsername(username.getAsString());
            if (user == null) {
                setParamWarnRes(res, "用户不存在", ErrorCode.INVALID_USER);
                return;
            }

            if (!user.getPassword().equals(password.getAsString())) {
                setParamWarnRes(res, "密码错误.", ErrorCode.INVALID_USER);
                return;
            }

            if (user.getStatus() == UserStatus.CLOSE) {
                setParamWarnRes(res, "用户被禁用.", ErrorCode.USER_CLOSE);
                return;
            }

            School school = schoolService.selSchoolBySchoolId(user.getSchool_id());
            ClassInf classInf = schoolService.selClassByClassId(user.getClass_id());

            TokenHandler tokenHandler = new TokenHandler(token_secret);
            User token_user = new User();
            token_user.setUser_id(user.getUser_id());
            token_user.setUsername(user.getUsername());
            token_user.setPassword(user.getPassword());
            token_user.setExpires(System.currentTimeMillis() + TEN_DAYS);
            String token = tokenHandler.createTokenForUser(token_user);

            res.addHeader(AUTH_HEADER_NAME, token);
            JsonObject json = new JsonObject();
            json.add("user_id", gson.toJsonTree(user.getUser_id()));
            json.add("username", gson.toJsonTree(user.getUsername()));
            json.add("full_name", gson.toJsonTree(user.getFull_name()));
            if (school != null) {
                json.add("school_name", gson.toJsonTree(school.getName()));
            }
            if (classInf != null) {
                json.add("class_name", gson.toJsonTree(classInf.getName()));
            }
            json.add("avatar_url", gson.toJsonTree(user.getAvatar_url()));
            content.setSuccess_message(json);
            setResponseContent(res, content);
        } catch (JsonSyntaxException e) {
            setRequestErrorRes(res, "JSON格式错误", ErrorCode.REQUEST_PARAM_FAORMAT_ERROR);
        } catch (Exception e) {
            setSystemErrorRes(res, e.toString(), ErrorCode.SYSTEM_ERROR);
        }
    }

    /*3.3.2	POST /api/user/logout*/
    @RequestMapping(value = "/api/user/logout", method = RequestMethod.POST)
    public void PostUserLogout(HttpServletRequest req, HttpServletResponse res) {
        ResponseContent content = new ResponseContent();
        try {
            User user = (User) req.getAttribute("user");
            /*PASS*/
            content.setSuccess_message(new Object());
            setResponseContent(res, content);
        } catch (JsonSyntaxException e) {
            setRequestErrorRes(res, "JSON格式错误", ErrorCode.REQUEST_PARAM_FAORMAT_ERROR);
        } catch (Exception e) {
            setSystemErrorRes(res, e.toString(), ErrorCode.SYSTEM_ERROR);
        }
    }

    @RequestMapping(value = "/api/user/health")
    public void CheckURLHealth(HttpServletRequest req, HttpServletResponse res) {
        ResponseContent content = new ResponseContent();
        try {
            User user = (User) req.getAttribute("user");
            content.setSuccess_message(user);
            setResponseContent(res, content);
        } catch (JsonSyntaxException e) {
            setRequestErrorRes(res, "JSON格式错误", ErrorCode.REQUEST_PARAM_FAORMAT_ERROR);
        } catch (Exception e) {
            setSystemErrorRes(res, e.toString(), ErrorCode.SYSTEM_ERROR);
        }
    }

    @RequestMapping(value = "/api/user/password", method = RequestMethod.PUT)
    public void PutUserPassword(HttpServletRequest req, HttpServletResponse res) {
        ResponseContent content = new ResponseContent();
        try {
            User user = (User) req.getAttribute("user");
            JsonObject params = getJsonElementParams(req).getAsJsonObject();
            String necessaryParams[] = {"old_password", "new_password"};
            if (!checkNecessaryParam(params, necessaryParams)) {
                setRequestErrorRes(res, "缺少必要请求参数", ErrorCode.REQUEST_PARAM_MISS);
                return;
            }
            String oldPwd = params.get("old_password").getAsString();
            String newPwd = params.get("new_password").getAsString();

            if (StringUtils.isBlank(oldPwd) || StringUtils.isBlank(newPwd)) {
                setRequestErrorRes(res, "缺少必要请求参数", ErrorCode.REQUEST_PARAM_MISS);
                return;
            }
            String db_password = user.getPassword();

            if (!(db_password.equals(oldPwd))) {
                setParamWarnRes(res, "旧密码错误", ErrorCode.INVALID_OLDPWD);
                return;
            }

            Map map = new HashMap<String, Object>();
            map.put("user_id", user.getUser_id());
            map.put("password", newPwd);
            userService.updateUser(map);
            content.setSuccess_message(null);
            setResponseContent(res, content);
        } catch (JsonSyntaxException e) {
            setRequestErrorRes(res, "JSON格式错误", ErrorCode.REQUEST_PARAM_FAORMAT_ERROR);
        } catch (Exception e) {
            setSystemErrorRes(res, e.toString(), ErrorCode.SYSTEM_ERROR);
        }
    }

    /*3.3.3	POST /api/user/push_service*/
    @RequestMapping(value = "/api/user/push_service", method = RequestMethod.POST)
    public void PostUserPushService(HttpServletRequest req, HttpServletResponse res) {
        ResponseContent content = new ResponseContent();
        try {
            User user = (User) req.getAttribute("user");
            JsonObject params = getJsonElementParams(req).getAsJsonObject();
            String necessaryParams[] = {"push_alias", "registration_id"};
            if (!checkNecessaryParam(params, necessaryParams)) {
                setRequestErrorRes(res, "缺少必要请求参数", ErrorCode.REQUEST_PARAM_MISS);
                return;
            }

            String push_alias = params.get("push_alias").getAsString();
            String registration_id = params.get("registration_id").getAsString();

            Map map = new HashMap(3);
            map.put("user_id", user.getUser_id());
            map.put("push_alias", push_alias);
            map.put("registration_id", registration_id);
            userService.updateUser(map);

            content.setSuccess_message(new Object());
            setResponseContent(res, content);
        } catch (JsonSyntaxException e) {
            setRequestErrorRes(res, "JSON格式错误", ErrorCode.REQUEST_PARAM_FAORMAT_ERROR);
        } catch (Exception e) {
            setSystemErrorRes(res, e.toString(), ErrorCode.SYSTEM_ERROR);
        }
    }
}