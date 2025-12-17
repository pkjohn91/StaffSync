import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../api/axiosConfig';
// import axios from 'axios';

const RegisterPage = () => {
  const navigate = useNavigate();
  const [step, setStep] = useState(1); // 1: 이메일, 2: 인증 코드, 3: 정보 입력
  const [formData, setFormData] = useState({
    email: '',
    verificationCode: '',
    name: '',
    password: '',
    confirmPassword: ''
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // 타이머 상태
  const [remainingTime, setRemainingTime] = useState(0); // 남은 시간 (초)
  const [timerInterval, setTimerInterval] = useState(null);

  // 이메일 정규식
  const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  const isEmailValid = emailRegex.test(formData.email);

  const handleChange = (e) => {
    setFormData({ 
      ...formData,
      [e.target.name]: e.target.value
    });
    setError('');
  };

  // 타이머 시작 함수
  const startTimer = (seconds) => {
    setRemainingTime(seconds);
    
    // 기존 타이머가 있으면 제거
    if (timerInterval) {
      clearInterval(timerInterval);
    }
    
    // 1초마다 감소
    const interval = setInterval(() => {
      setRemainingTime((prev) => {
        if (prev <= 1) {
          clearInterval(interval);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);
    
    setTimerInterval(interval);
  };

  // 컴포넌트 언마운트 시 타이머 정리
  useEffect(() => {
    return () => {
      if (timerInterval) {
        clearInterval(timerInterval);
      }
    };
  }, [timerInterval]);

  // 타이머 포맷 (mm:ss)
  const formatTime = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  /**
   * Step 1: 이메일 인증 코드 발송
   */
  const handleSendCode = async (e) => {
    e.preventDefault();
    
    if (!formData.email.trim()) {
      setError('이메일을 입력해주세요.');
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(formData.email)) {
      setError('올바른 이메일 형식이 아닙니다.');
      return;
    }

    setLoading(true);
    setError('');
    setSuccess('');

    try {
      const response = await api.post(
        `http://localhost:8080/api/members/send-code?email=${formData.email}`
      );
      
      setSuccess(response.data.message || '인증 코드가 발송되었습니다.');
      setStep(2);
      
      // ✅ 타이머 시작 (10분 = 600초)
      startTimer(600);
    } catch (error) {
      setError(error.response?.data || '인증 코드 발송에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  /**
   * ✅ 인증 코드 재발송
   */
  const handleResendCode = async () => {
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      const response = await api.post(
        `http://localhost:8080/api/members/send-code?email=${formData.email}`
      );
      
      setSuccess('인증 코드가 재발송되었습니다.');
      
      // 타이머 재시작
      startTimer(600);
    } catch (error) {
      setError(error.response?.data || '인증 코드 재발송에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  /**
   * Step 2: 인증 코드 검증
   */
  const handleVerifyCode = async (e) => {
    e.preventDefault();

    if (!formData.verificationCode.trim()) {
      setError('인증 코드를 입력해주세요.');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const response = await api.post(
        `http://localhost:8080/api/members/verify-code?email=${formData.email}&code=${formData.verificationCode}`
      );

      if (response.data.success) {
        setSuccess('인증이 완료되었습니다!');
        setStep(3);
        
        // ✅ 타이머 정지
        if (timerInterval) {
          clearInterval(timerInterval);
        }
      } else {
        setError(response.data.message || '인증 코드가 일치하지 않습니다.');
      }
    } catch (error) {
      setError(error.response?.data || '인증 코드 검증에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  /**
   * Step 3: 회원가입 완료
   */
  const handleRegister = async (e) => {
    e.preventDefault();

    if (!formData.name.trim()) {
      setError('이름을 입력해주세요.');
      return;
    }

    if (formData.password.length < 6) {
      setError('비밀번호는 최소 6자 이상이어야 합니다.');
      return;
    }

    if (formData.password !== formData.confirmPassword) {
      setError('비밀번호가 일치하지 않습니다.');
      return;
    }

    setLoading(true);
    setError('');

    try {
      await api.post('http://localhost:8080/api/members/register', formData);
      
      alert('✅ 회원가입이 완료되었습니다!');
      navigate('/login');
    } catch (error) {
      setError(error.response?.data || '회원가입에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center p-4">
      <div className="max-w-md w-full">
        {/* 로고 */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-16 h-16 bg-indigo-600 rounded-full mb-4">
            <svg className="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z" />
            </svg>
          </div>
          <h1 className="text-3xl font-bold text-gray-900">회원가입</h1>
          <p className="text-gray-600 mt-2">StaffSync에 오신 것을 환영합니다</p>
        </div>

        {/* 진행 상태 표시 */}
        <div className="bg-white rounded-lg shadow-xl p-8">
          <div className="flex justify-between mb-8">
            {[1, 2, 3].map((s) => (
              <div key={s} className="flex items-center">
                <div
                  className={`w-10 h-10 rounded-full flex items-center justify-center font-bold ${
                    step >= s
                      ? 'bg-indigo-600 text-white'
                      : 'bg-gray-200 text-gray-500'
                  }`}
                >
                  {s}
                </div>
                {s < 3 && (
                  <div
                    className={`w-16 h-1 ${
                      step > s ? 'bg-indigo-600' : 'bg-gray-200'
                    }`}
                  />
                )}
              </div>
            ))}
          </div>

          {/* 에러/성공 메시지 */}
          {error && (
            <div className="bg-red-50 border-l-4 border-red-400 p-4 mb-6">
              <p className="text-sm text-red-700">{error}</p>
            </div>
          )}

          {success && (
            <div className="bg-green-50 border-l-4 border-green-400 p-4 mb-6">
              <p className="text-sm text-green-700">{success}</p>
            </div>
          )}

          {/* Step 1: 이메일 입력 */}
          {step === 1 && (
            <form onSubmit={handleSendCode}>
              <h2 className="text-xl font-bold text-gray-900 mb-4">이메일 입력</h2>
              <div className="mb-6">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  이메일
                </label>
                <input
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                  placeholder="example@email.com"
                  required
                />
              </div>

              <button
                type="submit"
                disabled={loading}
                className="w-full bg-indigo-600 text-white py-3 px-4 rounded-lg font-medium hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                {loading ? '발송 중...' : '인증 코드 발송'}
              </button>
            </form>
          )}

          {/* Step 2: 인증 코드 입력 */}
          {step === 2 && (
            <form onSubmit={handleVerifyCode}>
              <h2 className="text-xl font-bold text-gray-900 mb-4">인증 코드 확인</h2>
              <p className="text-sm text-gray-600 mb-4">
                {formData.email}로 발송된 인증 코드를 입력하세요.
              </p>
              
              {/* ✅ 타이머 표시 */}
              {remainingTime > 0 && (
                <div className="bg-blue-50 border border-blue-200 rounded-lg p-3 mb-4">
                  <div className="flex items-center justify-between">
                    <span className="text-sm text-blue-800">
                      ⏱️ 남은 시간
                    </span>
                    <span className={`text-lg font-bold ${
                      remainingTime <= 60 ? 'text-red-600' : 'text-blue-600'
                    }`}>
                      {formatTime(remainingTime)}
                    </span>
                  </div>
                </div>
              )}
              
              {remainingTime === 0 && (
                <div className="bg-red-50 border border-red-200 rounded-lg p-3 mb-4">
                  <p className="text-sm text-red-700">
                    ⚠️ 인증 코드가 만료되었습니다. 재발송 버튼을 눌러주세요.
                  </p>
                </div>
              )}

              <div className="mb-6">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  인증 코드
                </label>
                <input
                  type="text"
                  name="verificationCode"
                  value={formData.verificationCode}
                  onChange={handleChange}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent text-center text-2xl tracking-widest"
                  placeholder="000000"
                  maxLength="6"
                  required
                />
              </div>

              <div className="flex gap-2">
                <button
                  type="button"
                  onClick={handleResendCode}
                  disabled={loading}
                  className="flex-1 border border-indigo-600 text-indigo-600 py-3 px-4 rounded-lg font-medium hover:bg-indigo-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                >
                  재발송
                </button>
                <button
                  type="submit"
                  disabled={loading || remainingTime === 0}
                  className="flex-1 bg-indigo-600 text-white py-3 px-4 rounded-lg font-medium hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                >
                  {loading ? '확인 중...' : '인증 확인'}
                </button>
              </div>
            </form>
          )}

          {/* Step 3: 정보 입력 */}
          {step === 3 && (
            <form onSubmit={handleRegister}>
              <h2 className="text-xl font-bold text-gray-900 mb-4">정보 입력</h2>

              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  이름
                </label>
                <input
                  type="text"
                  name="name"
                  value={formData.name}
                  onChange={handleChange}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                  placeholder="홍길동"
                  required
                />
              </div>

              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  비밀번호
                </label>
                <input
                  type="password"
                  name="password"
                  value={formData.password}
                  onChange={handleChange}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                  placeholder="최소 6자 이상"
                  required
                />
              </div>

              <div className="mb-6">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  비밀번호 확인
                </label>
                <input
                  type="password"
                  name="confirmPassword"
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                  placeholder="비밀번호 재입력"
                  required
                />
              </div>

              <button
                type="submit"
                disabled={loading}
                className="w-full bg-indigo-600 text-white py-3 px-4 rounded-lg font-medium hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                {loading ? '가입 중...' : '회원가입 완료'}
              </button>
            </form>
          )}

          {/* 로그인 링크 */}
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