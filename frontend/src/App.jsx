import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
// 상품 관리자 페이지
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/Product/RegisterPage';
import DashboardPage from './pages/Product/DashboardPage';
import ProductCreatePage from './pages/Product/ProductCreatePage';
import ProductEditPage from './pages/Product/ProductEditPage';
// 직원 페이지
import EmployeeCreatePage from './pages/Employee/EmployeeCreatePage';
import EmployeeEditPage from './pages/Employee/EmployeeEditPage';
// import EmployeeDetailPage from './pages/Employee/EmployeeDetailPage';
import EmployeeListPage from './pages/Employee/EmployeeListPage';
import ProtectedRoute from './components/ProtectedRoute';




function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<LoginPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        
        {/* 보호된 라우트 */}
        <Route 
          path="/dashboard" 
          element={
            <ProtectedRoute>
              <DashboardPage />
            </ProtectedRoute>
          } 
        />
        
        {/* 상품 등록 페이지 */}
        <Route 
          path="/products/create" 
          element={
            <ProtectedRoute>
              <ProductCreatePage />
            </ProtectedRoute>
          } 
        />

        <Route 
          path="/products/edit/:id"
          element={
            <ProtectedRoute>
              <ProductEditPage />
            </ProtectedRoute>
          }
        />

        {/* 직원 관리 */}
        <Route 
          path="/employees" 
          element={
            <ProtectedRoute>
              <EmployeeListPage />
            </ProtectedRoute>
          } 
        />
        
        <Route 
          path="/employees/create"
          element={
            <ProtectedRoute>
              <EmployeeCreatePage />
            </ProtectedRoute>
          } 
        />

        <Route 
          path="/employees/edit/:id"
          element={
            <ProtectedRoute>
              <EmployeeEditPage />
            </ProtectedRoute>
          }
        />

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Router>
  );
}

export default App;