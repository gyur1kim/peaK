package com.ssafy.peak.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.peak.dto.JwtTokenDto;
import com.ssafy.peak.dto.SignupDto;
import com.ssafy.peak.dto.UserDto;
import com.ssafy.peak.dto.idol.response.IdolCommentResponseDto;
import com.ssafy.peak.dto.request.WriteCommentRequestDto;
import com.ssafy.peak.dto.response.SuccessResponseDto;
import com.ssafy.peak.security.JwtTokenProvider;
import com.ssafy.peak.security.UserPrincipal;
import com.ssafy.peak.service.UserService;
import com.ssafy.peak.util.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

	private final UserService userService;
	private final JwtTokenProvider jwtTokenProvider;

	/**
	 * 닉네임 유효성 검사
	 */
	@GetMapping("/nickname/{nickname}")
	public ResponseEntity checkNickname(@PathVariable("nickname") String nickname) {

		userService.checkNickname(nickname);
		return ResponseEntity.ok(new SuccessResponseDto("사용 가능한 닉네임입니다."));
	}

	/**
	 * 회원 가입
	 * (OAuth2 login 성공 후)
	 */
	@PostMapping("/sign-up")
	public ResponseEntity signup(
		@RequestHeader("Authorization") String token,
		@RequestBody UserDto userRequestDto) {

		token = token.split(Utils.BLANK)[1];
		SignupDto signupDto = userService.signup(token, userRequestDto);

		return ResponseEntity.ok().body(signupDto);
	}

	/**
	 * 닉네임 수정
	 */
	@PutMapping("/nickname/{nickname}")
	public ResponseEntity modifyNickname(@PathVariable("nickname") String nickname) {

		userService.modifyNickname(nickname);
		return ResponseEntity.ok().body(new SuccessResponseDto("닉네임이 변경되었습니다."));
	}

	/**
	 * 로그아웃
	 */
	@PostMapping("/logout")
	public ResponseEntity logout(@RequestHeader("Authorization") String authrization) {

		String token = authrization.split(Utils.BLANK)[1];
		userService.logout(token);
		return ResponseEntity.ok().body(new SuccessResponseDto("로그아웃 되었습니다."));
	}

	/**
	 * 액세스 토큰 재발급
	 */
	@PostMapping("/reissue")
	@Transactional
	public ResponseEntity reissue(@RequestHeader("Authorization") String authorization) {

		// header에 token 꺼내서 Bearer 떼고 재발급 하러 가기
		String token = authorization.split(Utils.BLANK)[1];
		JwtTokenDto tokenDto = jwtTokenProvider.reissue(token);

		return ResponseEntity.ok().body(tokenDto);
	}

	/**
	 * 아이돌에게 응원 한 마디
	 */
	@PostMapping("/comment/{idol-name}")
	public ResponseEntity createCheeringMessage(
		@AuthenticationPrincipal UserPrincipal loginUser,
		@PathVariable("idol-name") String idolName,
		@RequestBody WriteCommentRequestDto writeCommentRequestDto) {

		userService.createCheeringMessage(loginUser, idolName, writeCommentRequestDto.getContent());
		return ResponseEntity.ok().body(new SuccessResponseDto("응원 메시지를 남겼습니다"));
	}

	/**
	 * 최근 2주 동안 내가 남긴 관심 아이돌 별 응원 메시지
	 */
	@GetMapping("/comment/{idol-name}")
	public ResponseEntity getMyCheeingMessages(
		@AuthenticationPrincipal UserPrincipal loginUser, @PathVariable("idol-name") String idolName) {

		IdolCommentResponseDto idolCommentResponseDto = userService.getMyCheeingMessages(loginUser, idolName);
		if (idolCommentResponseDto == null) {
			return ResponseEntity.ok().body(new SuccessResponseDto("최근 2주 동안의 응원 메시지가 없습니다."));
		} else {
			return ResponseEntity.ok().body(idolCommentResponseDto);
		}
	}
}
