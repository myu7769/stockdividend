package zerobase.stockdividend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zerobase.stockdividend.model.constants.Auth;
import zerobase.stockdividend.security.TokenProvider;
import zerobase.stockdividend.service.MemberService;


@Slf4j
@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    @PostMapping("/signup") // 회원 가입 api
    public ResponseEntity<?> signup(@RequestBody Auth.SignUp request) {
        var result = this.memberService.register(request);
        return ResponseEntity.ok(result);
    }
    @PostMapping("/signin") // 로그인 api
    public ResponseEntity<?> signin(@RequestBody Auth.SignIn request) {
        // 패스워드 검증
        var member = this.memberService.authenticate(request);

        // token 생성 후 반환
        var token = this.tokenProvider.generateToken(member.getUsername(), member.getRoles());

        return ResponseEntity.ok(token);
    }



}
