package com.example.oauth2rawtest.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Controller
public class OAuth2RawTest {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HttpServletRequest request;

    @GetMapping("/")
    public String run() {

        /**
         * 1. 구글이 정해준 URL로 로그인
         * 2. 내가 정한 리다이렉션 url로 구글이 code를 url parameter로 보내줌.
         * 3. 해당 코드를 가지고 토큰 get
         * 4. 토큰을 이용하여 유저 정보 get
         *
         */


        return "index";
    }

    /**
     * http://localhost:8080/code?code=4%2F4gGhgeFrWS3yxJ1V1zNq9YaMRVfnRY4KKb8tHpDfI6GOY6XSeio8c6XfV-W3EY0aY3eM8fb4itoxQzWL1Yhfb3s&scope=email+profile+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile+openid&authuser=0&prompt=consent#
     *
     * 코드는 위와같이 보내줌 구글에서
     *
     * @return
     */
    @GetMapping("/code")
    @ResponseBody
    public Object code(CodeResponse codeResponse) {
        Map<String, String[]> parameterMap = request.getParameterMap();

        for (String k : parameterMap.keySet()) {
            System.out.println("key: " + k);
            System.out.println("val: " + Arrays.toString(parameterMap.get(k)));
            System.out.println("-------------------------------");
            /**
             *
             * key: code
             * val: [4/4gEFtvdPnwfmtYUyt2BK7rs8q8ZWnjO8tv7fUlSfYuVswpo0p_ivv-BYFGZitPLRdtMUlXYKUVF4ejbPlonaE8o]
             * -------------------------------
             * key: scope
             * val: [email profile https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile openid]
             * -------------------------------
             * key: authuser
             * val: [0]
             * -------------------------------
             * key: prompt
             * val: [consent]
             * -------------------------------
             *
             */
        }

        getAccessToken(codeResponse);

        return parameterMap;

    }

    /**
     *
     * token 요청
     * POST /token HTTP/1.1
     * Host: oauth2.googleapis.com
     * Content-Type: application/x-www-form-urlencoded
     *
     * code=4/P7q7W91a-oMsCeLvIaQm6bTrgtp7&
     * client_id=your_client_id&
     * client_secret=your_client_secret&
     * redirect_uri=urn%3Aietf%3Awg%3Aoauth%3A2.0%3Aoob%3Aauto&
     * grant_type=authorization_code
     */
    public void getAccessToken(CodeResponse codeResponse) {

        Map<String, String> data = new HashMap<>();
        data.put("client_id", "186323170457-u6ndnjb2s9ocehvmiinr6rfh6494heqq.apps.googleusercontent.com");
        data.put("client_secret", "UyAeHQoAhl_py2BbTD1YEjXj");
        data.put("code", codeResponse.code);
        data.put("grant_type", "authorization_code");
        data.put("redirect_uri", "http://localhost:8080/code");

        Map<String, String> s = restTemplate.postForObject("https://oauth2.googleapis.com/token", data, Map.class);

        for (String k : s.keySet()) {
            System.out.println("key: " + k);
            System.out.println("val: " + s.get(k));
            System.out.println("-------------------------------");
        }

        /**
         * {
         *   "access_token": "ya29.a0AfH6SMAkmgVy-KJKYbvEsP9j8lpM8o_48zhoLUuIvCVs08r3wqxM8guwV5DulzbG3NhYe99mLtMFMiDiXiSpcE20Fs83Si086IR-EVx0HDE-Ge2-96m63dDoLZr7IbAE5kZ3eFWF5i0msSdyl-ntUHRBPQmC-Ew2I48",
         *   "expires_in": 3599,
         *   "scope": "openid https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile",
         *   "token_type": "Bearer",
         *   "id_token": "eyJhbGciOiJSUzI1NiIsImtpZCI6IjJjNmZhNmY1OTUwYTdjZTQ2NWZjZjI0N2FhMGIwOTQ4MjhhYzk1MmMiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiIxODYzMjMxNzA0NTctdTZuZG5qYjJzOW9jZWh2bWlpbnI2cmZoNjQ5NGhlcXEuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiIxODYzMjMxNzA0NTctdTZuZG5qYjJzOW9jZWh2bWlpbnI2cmZoNjQ5NGhlcXEuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMTM4MTM5MDE4NDg1MzU0NjM4NjkiLCJlbWFpbCI6ImlybmQwNEBnbWFpbC5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiYXRfaGFzaCI6ImpZNGpnOVFJeGlIT0ZJeUFZWHJtSWciLCJuYW1lIjoi7J207J6s6rWtIiwicGljdHVyZSI6Imh0dHBzOi8vbGg1Lmdvb2dsZXVzZXJjb250ZW50LmNvbS8td3o1SXFmeS1TRVUvQUFBQUFBQUFBQUkvQUFBQUFBQUFBQUEvQU1adXVjbEtITVZ4eC16X1hXSXdWaThmdTZfeEFtRjg0QS9zOTYtYy9waG90by5qcGciLCJnaXZlbl9uYW1lIjoi7J6s6rWtIiwiZmFtaWx5X25hbWUiOiLsnbQiLCJsb2NhbGUiOiJrbyIsImlhdCI6MTYwMTQ3MDYyMiwiZXhwIjoxNjAxNDc0MjIyfQ.dVXs4969PucqL2LiAelm_zGub5dwAIFn62eiH-Lvi1crjrE9Vncapz01FW0VgS4gEu7SQwKH7vk7KIZm5WEk1m7WZrebxgoSh9F-rKssCwfAP6XMsvlxQS4woLVBgIX8dI6MU1frJnaSTky8tCmDYLIRPd3FqAl9Wxb7mIO2_xFePCIQa-xvVdklg1Jb3CuFjEA3JpqI4xCC3lpI4aB81rsfGN-BDfIZ92qGVaBm8dnrkTQWOETRRwbvWHG5tXtnWrsS8bkuFa8zXpKlKCTsDxinss5zfNwzKDRGcMTzhKTEsUn5EVYHY1znoYENC9effPuLSbzxQy90NIdr0ejrgg"
         * }
         *
         */

        // userinfo get
        // https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=ya29.a0AfH6SMAkmgVy-KJKYbvEsP9j8lpM8o_48zhoLUuIvCVs08r3wqxM8guwV5DulzbG3NhYe99mLtMFMiDiXiSpcE20Fs83Si086IR-EVx0HDE-Ge2-96m63dDoLZr7IbAE5kZ3eFWF5i0msSdyl-ntUHRBPQmC-Ew2I48

        String r =
                restTemplate.getForObject("https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=" + s.get("access_token"), String.class);
        System.out.println(r);


    }

}
