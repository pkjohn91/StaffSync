import RegisterPage from "./components/RegisterPage";

function App() {
  return (
    <div>
      <RegisterPage />
    </div>
  );
}

export default App;

/*

import { useState, useEffect } from 'react';
import axios from 'axios';

// API 주소 (Spring Boot)
const API_URL = "http://localhost:8080/api/employees";

function App() {
  const [employees, setEmployees] = useState([]);
  const [form, setForm] = useState({ name: '', position: '', department: '', salary: '' });
  const [loading, setLoading] = useState(false);

  // 1. 데이터 불러오기 (Read)
  const fetchEmployees = async () => {
    try {
      setLoading(true);
      const response = await axios.get(API_URL);
      setEmployees(response.data);
    } catch (error) {
      console.error("Error fetching data:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchEmployees();
  }, []);

  // 2. 입력 값 핸들링
  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  // 3. 직원 등록 (Create)
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.name || !form.position) return alert("이름과 직책은 필수입니다.");

    try {
      await axios.post(API_URL, form);
      setForm({ name: '', position: '', department: '', salary: '' }); // 폼 초기화
      fetchEmployees(); // 목록 갱신
    } catch (error) {
      console.error("Error adding employee:", error);
    }
  };

  // 4. 직원 삭제 (Delete)
  const handleDelete = async (id) => {
    if (window.confirm("정말 삭제하시겠습니까?")) {
      try {
        await axios.delete(`${API_URL}/${id}`);
        setEmployees(employees.filter(emp => emp.id !== id)); // UI에서 즉시 제거 (Optimistic Update 효과)
      } catch (error) {
        console.error("Error deleting:", error);
      }
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 p-10">
      <div className="max-w-4xl mx-auto">
        <h1 className="text-3xl font-bold text-blue-600 mb-8 text-center">🏢 StaffSync HR System</h1>
*/

/*
        {/* 입력 폼 }
        <div className="bg-white p-6 rounded-lg shadow-md mb-8">
          <h2 className="text-xl font-semibold mb-4">새 직원 등록</h2>
          <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <input name="name" placeholder="이름" value={form.name} onChange={handleChange} className="border p-2 rounded" />
            <input name="position" placeholder="직책 (예: Developer)" value={form.position} onChange={handleChange} className="border p-2 rounded" />
            <input name="department" placeholder="부서 (예: IT)" value={form.department} onChange={handleChange} className="border p-2 rounded" />
            <input name="salary" type="number" placeholder="급여" value={form.salary} onChange={handleChange} className="border p-2 rounded" />
            <button type="submit" className="md:col-span-2 bg-blue-500 text-white p-2 rounded hover:bg-blue-600 transition">
              등록하기
            </button>
          </form>
        </div>

        {/* 직원 목록 테이블 }
        <div className="bg-white p-6 rounded-lg shadow-md">
          <h2 className="text-xl font-semibold mb-4">직원 목록 ({employees.length}명)</h2>
          {loading ? (
            <p className="text-center text-gray-500">로딩 중...</p>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left border-collapse">
                <thead>
                  <tr className="bg-gray-50 text-gray-700">
                    <th className="p-3 border-b">ID</th>
                    <th className="p-3 border-b">이름</th>
                    <th className="p-3 border-b">부서</th>
                    <th className="p-3 border-b">직책</th>
                    <th className="p-3 border-b">급여</th>
                    <th className="p-3 border-b text-right">관리</th>
                  </tr>
                </thead>
                <tbody>
                  {employees.map((emp) => (
                    <tr key={emp.id} className="hover:bg-gray-50 border-b">
                      <td className="p-3">{emp.id}</td>
                      <td className="p-3 font-medium">{emp.name}</td>
                      <td className="p-3">
                        <span className="bg-blue-100 text-blue-800 text-xs px-2 py-1 rounded-full">{emp.department}</span>
                      </td>
                      <td className="p-3">{emp.position}</td>
                      <td className="p-3">${emp.salary.toLocaleString()}</td>
                      <td className="p-3 text-right">
                        <button onClick={() => handleDelete(emp.id)} className="text-red-500 hover:text-red-700 text-sm">
                          삭제
                        </button>
                      </td>
                    </tr>
                  ))}
                  {employees.length === 0 && (
                    <tr>
                      <td colSpan="6" className="p-4 text-center text-gray-400">등록된 직원이 없습니다.</td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default App;
*/