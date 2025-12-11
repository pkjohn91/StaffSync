import { useState } from 'react';
import axios from 'axios';

const RegisterPage = () => {
  const [formData, setFormData] = useState({
    email: '',
    verificationCode: '',
    name: '',
    password: '',
  });

  const [isCodeSent, setIsCodeSent] = useState(false); // 코드 발송 여부
  const [isVerified, setIsVerified] = useState(false); // ✅ 인증 완료 여부 (핵심)
  const [loading, setLoading] = useState(false);

  // 이메일 정규식
  const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  const isEmailValid = emailRegex.test(formData.email);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  // 1. 인증 코드 발송 요청
  const handleSendCode = async () => {
    if (!isEmailValid) return alert("올바른 이메일 형식을 입력해주세요.");

    try {
      setLoading(true);
      await axios.post(`http://localhost:8080/api/members/send-code?email=${formData.email}`);
      alert("인증 코드가 발송되었습니다! (백엔드 콘솔 확인)");
      setIsCodeSent(true);
      setIsVerified(false); // 재전송 시 인증 상태 초기화
    } catch (error) {
      alert(error.response?.data || "이메일 발송 실패");
    } finally {
      setLoading(false);
    }
  };

  // ✅ 2. 인증 코드 확인 요청 (새로 추가된 기능)
  const handleVerifyCode = async () => {
    if (!formData.verificationCode) return alert("코드를 입력해주세요.");

    try {
      // 백엔드에 코드 검증 요청
      await axios.post(`http://localhost:8080/api/members/verify-code`, null, {
        params: {
          email: formData.email,
          code: formData.verificationCode
        }
      });

      // 성공 시
      alert("✅ 확인되었습니다.");
      setIsVerified(true); // 이제 이름/비번 입력칸이 풀립니다!
    } catch (error) {
      alert(error.response?.data || "인증 실패");
      setIsVerified(false);
    }
  };

  // 3. 최종 회원가입 요청
  const handleRegister = async (e) => {
    e.preventDefault();

    if (!isVerified) return alert("이메일 인증을 먼저 완료해주세요.");

    try {
      await axios.post("http://localhost:8080/api/members/register", formData);
      alert("🎉 회원가입 성공! 로그인 페이지로 이동합니다.");
      window.location.reload();
    } catch (error) {
      const errorData = error.response?.data;
      if (typeof errorData === 'object') {
        alert(Object.values(errorData).join('\n'));
      } else {
        alert(errorData || "회원가입 실패");
      }
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <div className="bg-white p-8 rounded-lg shadow-md w-96">
        <h2 className="text-2xl font-bold text-center text-blue-600 mb-6">회원가입</h2>

        <form onSubmit={handleRegister} className="space-y-4">
          
          {/* 1. 이메일 입력 */}
          <div className="flex gap-2">
            <input
              type="email"
              name="email"
              placeholder="이메일"
              value={formData.email}
              onChange={handleChange}
              className={`flex-1 border p-2 rounded outline-none ${
                isEmailValid ? 'border-green-500' : 'border-gray-300'
              }`}
              // 코드가 발송되면 이메일 수정 불가 (재전송하려면 새로고침 유도 or 별도 버튼 필요)
              disabled={isCodeSent} 
            />
            <button
              type="button"
              onClick={handleSendCode}
              disabled={!isEmailValid || loading || isCodeSent}
              className={`text-sm px-3 rounded text-white transition-colors ${
                !isEmailValid || isCodeSent ? 'bg-gray-400' : 'bg-blue-500 hover:bg-blue-600'
              }`}
            >
              {loading ? "전송 중" : isCodeSent ? "발송됨" : "인증"}
            </button>
          </div>

          {/* 2. 인증 코드 입력 & 확인 버튼 (발송된 경우에만 표시) */}
          {isCodeSent && (
            <div className="flex gap-2 animate-fade-in-down">
              <input
                type="text"
                name="verificationCode"
                placeholder="코드 6자리"
                value={formData.verificationCode}
                onChange={handleChange}
                // 인증 완료되면 코드 수정 불가
                disabled={isVerified} 
                className="flex-1 border p-2 rounded bg-blue-50 focus:ring-2 focus:ring-blue-300 outline-none"
              />
              <button
                type="button"
                onClick={handleVerifyCode}
                disabled={isVerified} // 이미 인증됐으면 버튼 비활성
                className={`text-sm px-3 rounded text-white whitespace-nowrap ${
                    isVerified ? 'bg-green-500' : 'bg-blue-600 hover:bg-blue-700'
                }`}
              >
                {isVerified ? "완료됨" : "확인"}
              </button>
            </div>
          )}

          {/* 3. 이름 (인증 전에는 비활성) */}
          <input
            type="text"
            name="name"
            placeholder="이름"
            value={formData.name}
            onChange={handleChange}
            // ✅ 인증 안 되면 입력 불가
            disabled={!isVerified} 
            className={`w-full border p-2 rounded transition-colors ${
                !isVerified ? 'bg-gray-100 cursor-not-allowed' : 'bg-white'
            }`}
          />

          {/* 4. 비밀번호 (인증 전에는 비활성) */}
          <input
            type="password"
            name="password"
            placeholder="비밀번호 (8자 이상)"
            value={formData.password}
            onChange={handleChange}
            // ✅ 인증 안 되면 입력 불가
            disabled={!isVerified}
            className={`w-full border p-2 rounded transition-colors ${
                !isVerified ? 'bg-gray-100 cursor-not-allowed' : 'bg-white'
            }`}
          />

          {/* 5. 가입 버튼 (인증 전에는 숨기거나 비활성) */}
          <button
            type="submit"
            disabled={!isVerified}
            className={`w-full p-3 rounded font-bold transition shadow-lg mt-4 ${
                isVerified 
                ? 'bg-green-500 text-white hover:bg-green-600' 
                : 'bg-gray-300 text-gray-500 cursor-not-allowed'
            }`}
          >
            가입하기
          </button>
        </form>
      </div>
    </div>
  );
};

export default RegisterPage;