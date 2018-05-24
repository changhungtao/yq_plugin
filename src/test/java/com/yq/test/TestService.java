package com.yq.test;

import com.yq.model.SignUpJob;
import com.yq.service.inf.SignUpJobService;

import javax.annotation.Resource;
import java.util.List;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:mvc.xml"})
public class TestService {
    @Resource
    private SignUpJobService signUpJobService;

//    @Test
    public void testJobService() throws Exception {
//        List<SignUpJob> jobs = signUpJobService.selectJobs();
//        for (SignUpJob job : jobs) {
//            String log = String.format("Test: %d, %d, %s, %s", job.getJob_id(), job.getYq_name(), job.getTriggerName(), job.getCronExpression());
//            System.out.println(log);
//        }
        return;
    }
}
