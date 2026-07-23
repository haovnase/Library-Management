# Library Management

Ứng dụng quản lý thư viện sử dụng Spring Boot, Thymeleaf, JPA và Java 21.

## Chạy trên localhost

Yêu cầu:

- Java 21
- Không cần cài Maven hoặc MySQL cho chế độ local

Mở PowerShell tại thư mục dự án và chạy:

```powershell
.\mvnw.cmd spring-boot:run
```

Truy cập:

- Ứng dụng: http://localhost:8080
- H2 Console: http://localhost:8080/h2-console

Thông tin H2 Console:

- JDBC URL: `jdbc:h2:file:./data/library-management`
- User: `sa`
- Password: để trống

## Tài khoản demo

| Vai trò | Tên đăng nhập | Mật khẩu |
|---|---|---|
| Độc giả | `customer` | `123456` |
| Thủ thư | `manager` | `123456` |
| Quản trị viên | `admin` | `123456` |

Dữ liệu local được lưu trong thư mục `data/` và được giữ lại sau khi tắt ứng dụng.

Muốn tạo lại dữ liệu mẫu từ đầu:

1. Tắt ứng dụng.
2. Xóa thư mục `data/`.
3. Chạy lại `.\mvnw.cmd spring-boot:run`.

## Chạy kiểm thử

```powershell
.\mvnw.cmd clean test
```

## Dùng MySQL

Kích hoạt profile `mysql`:

```powershell
$env:MYSQL_URL = "jdbc:mysql://localhost:3306/LibraryManagement"
$env:MYSQL_USERNAME = "root"
$env:MYSQL_PASSWORD = "your-password"
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=mysql"
```

Nếu không đặt biến môi trường, profile MySQL mặc định kết nối tới
`jdbc:mysql://localhost:3306/LibraryManagement` với user `root`.
