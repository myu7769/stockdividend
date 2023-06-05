package zerobase.stockdividend.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import zerobase.stockdividend.exception.impl.AlreadyExistUserException;
import zerobase.stockdividend.model.MemberEntity;
import zerobase.stockdividend.model.constants.Auth;
import zerobase.stockdividend.persist.MemberRepository;

@Slf4j
@Service
@AllArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.memberRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("유저를 찾을 수 없음" + username));
    }

    public MemberEntity register(Auth.SignUp member) {

        boolean exists = this.memberRepository.existsByUsername(member.getUsername());

        if (exists) {
            throw new AlreadyExistUserException();
        }

        member.setPassword(this.passwordEncoder.encode(member.getPassword()));
        var result = this.memberRepository.save(member.toEntity());

        return result;
    }

    public MemberEntity authenticate(Auth.SignIn member) {

        var user = this.memberRepository.findByUsername(member.getUsername()) // 회원 있는지 확인
                .orElseThrow(()-> new RuntimeException("존재 하지 않는 ID 입니다"));

        log.info("member password : " + member.getPassword());
        log.info("user password : " + user.getPassword());

        if (!this.passwordEncoder.matches(member.getPassword(), user.getPassword())) { // 비밀번호 검증
            throw new RuntimeException("비밀번호가 일치하지 않습니다");
        }

        return user;
    }
}
