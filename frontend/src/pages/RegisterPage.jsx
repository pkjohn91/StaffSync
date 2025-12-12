import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const RegisterPage = () => {
  const navigate = useNavigate();
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

  // ✅ 2. 인증 코드 확인 요청
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
      navigate('/login'); // ✅ 수정: 로그인 페이지로 이동
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
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-100 py-12 px-4">
      <div className="w-full max-w-md">
        
        {/* ✅ 추가: 로그인으로 돌아가기 버튼 */}
        <button
          onClick={() => navigate('/login')}
          className="mb-6 flex items-center text-indigo-600 hover:text-indigo-700 transition-colors font-medium"
        >
          <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
          로그인으로 돌아가기
        </button>

        {/* 회원가입 폼 */}
        <div className="bg-white p-8 rounded-lg shadow-xl">
          
          {/* 헤더 */}
          <div className="text-center mb-8">
            <h2 className="text-3xl font-bold text-gray-900">회원가입</h2>
            <p className="mt-2 text-sm text-gray-600">
              StaffSync에 오신 것을 환영합니다
            </p>
          </div>

          <form onSubmit={handleRegister} className="space-y-4">
            
            {/* 1. 이메일 입력 */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                이메일
              </label>
              <div className="flex gap-2">
                <input
                  type="email"
                  name="email"
                  placeholder="example@email.com"
                  value={formData.email}
                  onChange={handleChange}
                  className={`flex-1 border px-4 py-3 rounded-lg outline-none transition-colors ${
                    isEmailValid ? 'border-green-500 bg-green-50' : 'border-gray-300'
                  }`}
                  disabled={isCodeSent}
                />
                <button
                  type="button"
                  onClick={handleSendCode}
                  disabled={!isEmailValid || loading || isCodeSent}
                  className={`px-4 py-3 rounded-lg font-medium text-white transition-colors whitespace-nowrap ${
                    !isEmailValid || isCodeSent 
                      ? 'bg-gray-400 cursor-not-allowed' 
                      : 'bg-indigo-600 hover:bg-indigo-700'
                  }`}
                >
                  {loading ? "전송 중..." : isCodeSent ? "발송 완료" : "인증 코드"}
                </button>
              </div>
            </div>

            {/* 2. 인증 코드 입력 & 확인 버튼 */}
            {isCodeSent && (
              <div className="animate-fade-in-down">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  인증 코드
                </label>
                <div className="flex gap-2">
                  <input
                    type="text"
                    name="verificationCode"
                    placeholder="6자리 코드 입력"
                    value={formData.verificationCode}
                    onChange={handleChange}
                    disabled={isVerified}
                    className="flex-1 border px-4 py-3 rounded-lg bg-blue-50 focus:ring-2 focus:ring-indigo-500 outline-none"
                  />
                  <button
                    type="button"
                    onClick={handleVerifyCode}
                    disabled={isVerified}
                    className={`px-4 py-3 rounded-lg font-medium text-white whitespace-nowrap transition-colors ${
                      isVerified 
                        ? 'bg-green-500 cursor-not-allowed' 
                        : 'bg-indigo-600 hover:bg-indigo-700'
                    }`}
                  >
                    {isVerified ? "✓ 완료" : "확인"}
                  </button>
                </div>
              </div>
            )}

            {/* 3. 이름 */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                이름
              </label>
              <input
                type="text"
                name="name"
                placeholder="홍길동"
                value={formData.name}
                onChange={handleChange}
                disabled={!isVerified}
                className={`w-full border px-4 py-3 rounded-lg outline-none transition-colors ${
                  !isVerified 
                    ? 'bg-gray-100 cursor-not-allowed border-gray-200' 
                    : 'bg-white border-gray-300 focus:ring-2 focus:ring-indigo-500'
                }`}
              />
            </div>

            {/* 4. 비밀번호 */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                비밀번호
              </label>
              <input
                type="password"
                name="password"
                placeholder="8자 이상 입력"
                value={formData.password}
                onChange={handleChange}
                disabled={!isVerified}
                className={`w-full border px-4 py-3 rounded-lg outline-none transition-colors ${
                  !isVerified 
                    ? 'bg-gray-100 cursor-not-allowed border-gray-200' 
                    : 'bg-white border-gray-300 focus:ring-2 focus:ring-indigo-500'
                }`}
              />
            </div>

            {/* 5. 가입 버튼 */}
            <button
              type="submit"
              disabled={!isVerified}
              className={`w-full py-3 rounded-lg font-bold transition-all shadow-lg mt-6 ${
                isVerified 
                  ? 'bg-indigo-600 text-white hover:bg-indigo-700 hover:shadow-xl' 
                  : 'bg-gray-300 text-gray-500 cursor-not-allowed'
              }`}
            >
              {isVerified ? '가입하기' : '이메일 인증을 먼저 완료해주세요'}
            </button>
          </form>

          {/* 이미 계정이 있는 경우 */}
          <div className="mt-6 text-center">
            <p className="text-sm text-gray-600">
              이미 계정이 있으신가요?{' '}
              <button
                onClick={() => navigate('/login')}
                className="text-indigo-600 font-medium hover:text-indigo-700 transition-colors"
              >
                로그인
              </button>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RegisterPage;