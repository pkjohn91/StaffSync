import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import api from '../api/axiosConfig';
// import axios from 'axios';

const EmployeeEditPage = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    salary: 0,
    department: ''
  });
  const [employeeInfo, setEmployeeInfo] = useState(null); // 읽기 전용 정보
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const departments = ['개발팀', '디자인팀', '기획팀', '마케팅팀', '영업팀', '인사팀', '총무팀'];

  /**
   * 직원 정보 불러오기
   * GET /api/employees/{id}
   */
  useEffect(() => {
    const fetchEmployee = async () => {
      try {
        const response = await api.get(`http://localhost:8080/api/employees/${id}`);
        const employee = response.data;
        
        setEmployeeInfo({
          employeeId: employee.employeeId,
          hireDate: employee.hireDate
        });
        
        setFormData({
          name: employee.name,
          email: employee.email,
          salary: employee.salary,
          department: employee.department || ''
        });
      } catch (error) {
        alert('직원 정보를 불러오는데 실패했습니다: ' + (error.response?.data || error.message));
        navigate('/employees');
      } finally {
        setLoading(false);
      }
    };

    fetchEmployee();
  }, [id, navigate]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: name === 'salary' ? Number(value) : value
    });
  };

  /**
   * 직원 정보 수정
   * PUT /api/employees/{id}
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
      setSaving(true);
      await api.put(`http://localhost:8080/api/employees/${id}`, formData);
      alert('✅ 직원 정보가 수정되었습니다!');
      navigate('/employees');
    } catch (error) {
      alert('수정 실패: ' + (error.response?.data || error.message));
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">로딩 중...</p>
        </div>
      </div>
    );
  }

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
            <h2 className="text-3xl font-bold text-gray-900">직원 정보 수정</h2>
            <p className="text-gray-600 mt-2">직원 정보를 수정하세요</p>
          </div>

          {/* 읽기 전용 정보 표시 */}
          <div className="bg-blue-50 border-l-4 border-blue-400 p-4 mb-6">
            <div className="flex items-start">
              <svg className="w-5 h-5 text-blue-400 mt-0.5 mr-3" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clipRule="evenodd" />
              </svg>
              <div className="text-sm">
                <p className="font-medium text-blue-800">읽기 전용 정보</p>
                <p className="text-blue-700 mt-1">
                  <span className="font-semibold">사원번호:</span> {employeeInfo?.employeeId} | 
                  <span className="font-semibold ml-3">입사일:</span> {employeeInfo?.hireDate}
                </p>
              </div>
            </div>
          </div>

          {/* 수정 폼 */}
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

            {/* 부서 */}
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

            {/* 수정 정보 미리보기 */}
            <div className="bg-gray-50 rounded-lg p-4 border border-gray-200">
              <p className="text-sm font-medium text-gray-700 mb-2">수정할 정보</p>
              <div className="space-y-1 text-sm">
                <p><span className="font-medium">이름:</span> {formData.name}</p>
                <p><span className="font-medium">이메일:</span> {formData.email}</p>
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
                disabled={saving}
                className="flex-1 px-6 py-3 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {saving ? '수정 중...' : '수정 완료'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default EmployeeEditPage;