import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const EmployeeCreatePage = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    hireDate: new Date().toISOString().split('T')[0], // 오늘 날짜
    salary: 0,
    department: ''
  });
  const [loading, setLoading] = useState(false);

  const departments = ['개발팀', '디자인팀', '기획팀', '마케팅팀', '영업팀', '인사팀', '총무팀'];

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: name === 'salary' ? Number(value) : value
    });
  };

  /**
   * 직원 등록
   * POST /api/employees
   */
  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // 유효성 검사
    if (!formData.name.trim()) {
      alert('이름을 입력해주세요.');
      return;
    }
    if (!formData.email.trim()) {
      alert('이메일을 입력해주세요.');
      return;
    }
    if (formData.salary < 0) {
      alert('급여는 0 이상이어야 합니다.');
      return;
    }

    try {
      setLoading(true);
      await axios.post('http://localhost:8080/api/employees', formData);
      alert('✅ 직원이 등록되었습니다!');
      navigate('/employees');
    } catch (error) {
      alert('등록 실패: ' + (error.response?.data || error.message));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* 상단 네비게이션 */}
      <nav className="bg-white shadow-sm border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-8 py-4">
          <div className="flex justify-between items-center">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 bg-indigo-600 rounded-lg flex items-center justify-center">
                <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
                </svg>
              </div>
              <div>
                <h1 className="text-xl font-bold text-gray-900">StaffSync</h1>
                <p className="text-xs text-gray-500">직원 관리</p>
              </div>
            </div>

            <button
              onClick={() => navigate('/employees')}
              className="flex items-center gap-2 px-4 py-2 bg-gray-600 text-white rounded-lg hover:bg-gray-700 transition-colors"
            >
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
              </svg>
              목록으로
            </button>
          </div>
        </div>
      </nav>

      {/* 메인 콘텐츠 */}
      <div className="max-w-3xl mx-auto p-8">
        <div className="bg-white rounded-lg shadow-lg p-8">
          {/* 헤더 */}
          <div className="mb-8">
            <h2 className="text-3xl font-bold text-gray-900">새 직원 등록</h2>
            <p className="text-gray-600 mt-2">사원번호는 자동으로 생성됩니다.</p>
          </div>

          {/* 등록 폼 */}
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* 이름 */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                이름 <span className="text-red-500">*</span>
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

            {/* 이메일 */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                이메일 <span className="text-red-500">*</span>
              </label>
              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                placeholder="example@staffsync.com"
                required
              />
            </div>

            {/* 입사일 & 부서 */}
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  입사일 <span className="text-red-500">*</span>
                </label>
                <input
                  type="date"
                  name="hireDate"
                  value={formData.hireDate}
                  onChange={handleChange}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  부서
                </label>
                <select
                  name="department"
                  value={formData.department}
                  onChange={handleChange}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                >
                  <option value="">선택 안 함</option>
                  {departments.map((dept) => (
                    <option key={dept} value={dept}>{dept}</option>
                  ))}
                </select>
              </div>
            </div>

            {/* 급여 */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                급여 (₩) <span className="text-red-500">*</span>
              </label>
              <input
                type="number"
                name="salary"
                value={formData.salary}
                onChange={handleChange}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                placeholder="0"
                min="0"
                step="1000000"
                required
              />
            </div>

            {/* 미리보기 */}
            <div className="bg-gray-50 rounded-lg p-4 border border-gray-200">
              <p className="text-sm font-medium text-gray-700 mb-2">등록 정보 미리보기</p>
              <div className="space-y-1 text-sm">
                <p><span className="font-medium">이름:</span> {formData.name || '(입력 필요)'}</p>
                <p><span className="font-medium">이메일:</span> {formData.email || '(입력 필요)'}</p>
                <p><span className="font-medium">입사일:</span> {formData.hireDate}</p>
                <p><span className="font-medium">부서:</span> {formData.department || '미지정'}</p>
                <p><span className="font-medium">급여:</span> ₩{formData.salary.toLocaleString()}</p>
              </div>
            </div>

            {/* 버튼 */}
            <div className="flex gap-4">
              <button
                type="button"
                onClick={() => navigate('/employees')}
                className="flex-1 px-6 py-3 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
              >
                취소
              </button>
              <button
                type="submit"
                disabled={loading}
                className="flex-1 px-6 py-3 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {loading ? '등록 중...' : '직원 등록'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default EmployeeCreatePage;