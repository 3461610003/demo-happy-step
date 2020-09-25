package com.example.demo.happy.step.task;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
public class StepTaskTest {

    @Resource
    private StepTask stepTask;

    @Test
    public void task() throws InterruptedException {
//        stepTask.task();
    }

    @Test
    public void writeReadToken() {
        String accessToken = "D2A6AFB93531605DBE56DC2EEE74C4C9C7B227AD040AB9F11531814553FC1853764048F2CE04BA69BB8EB6EA9DCAE73FA848D8C9A1BA671F2ABE5E7C60331D0BA1D6D795CCC4E4404BA7951A43C6181CDD8C7BE9FB1BDFA9CAE98A5AF8D0D766.48C068BC29B3A9BF60D43B0407CFF11B6ACB1BE88D1F717940099D8308CFC909";
        String userId = "27231098";
//        String compressAccessToken = ZipUtil.compress(accessToken);
//        System.out.println(compressAccessToken);
//        String compressUserId = ZipUtil.compress(userId);
//        System.out.println(compressUserId);
//
//        System.out.println(ZipUtil.uncompress(compressAccessToken));
//        System.out.println(ZipUtil.uncompress(compressUserId));

//        String gzipB64AccessToken = ZipUtil.gzipB64(accessToken);
//        System.out.println(gzipB64AccessToken);
//        String gzipB64UserId = ZipUtil.gzipB64(userId);
//        System.out.println(gzipB64UserId);
//        System.out.println(ZipUtil.uncompress(gzipB64AccessToken));
//        System.out.println(ZipUtil.uncompress(gzipB64UserId));
        stepTask.writeToken(accessToken, userId);
        System.out.println(stepTask.readToken());
    }
}