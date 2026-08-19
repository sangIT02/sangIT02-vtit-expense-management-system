# Demo – Ứng dụng Quản lý Tài chính Cá nhân

REST API quản lý tài chính cá nhân: quản lý ví tiền, danh mục, giao dịch, ngân sách, mục tiêu; xác thực JWT; phân quyền theo role + permission lưu trong database.

---

## 1. Yêu cầu môi trường

| Công cụ | Phiên bản |
|---|---|
| JDK | 17 |
| Maven | 3.6+ (hoặc dùng `mvnw`/`mvnw.cmd` có sẵn) |
| MySQL | 8.0+ |
| Git | bất kỳ |

Tùy chọn (nếu không cấu hình, ứng dụng vẫn chạy nhưng có thể không sử dụng được):

- SMTP Gmail (gửi email OTP) – khai báo `MAIL_USERNAME`, `MAIL_PASSWORD`.
- Redis (Upstash/local) – khai báo `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`. Hiện tại chưa được dùng trong logic nghiệp vụ.

---

## 2. Cài đặt và chạy

### 2.1. Clone project

```bash
git clone <repository-url>
cd demo
```

### 2.2. Tạo database

```sql
CREATE DATABASE user_management_db;
```

Schema sẽ được tạo tự động bởi **Flyway** khi ứng dụng khởi động.

### 2.3. Cấu hình biến môi trường

Tạo file `.env` ở thư mục gốc dựa theo mẫu:

```bash
cp .env.example .env
```

> **Lưu ý bảo mật:** file `.env` chứa mật khẩu/secret và đã được thêm vào `.gitignore`. Không commit file này lên repository.

### 2.4. Build và chạy

**Cách 1 – dùng Maven wrapper:**

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

**Cách 2 – build rồi chạy JAR:**

```bash
mvn clean package
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

### 2.5. Kiểm tra

| Địa điểm | URL |
|---|---|
| API (Swagger UI) | http://localhost:8080/swagger-ui.html |
| OpenAPI docs (JSON) | http://localhost:8080/v3/api-docs |
| Health check cơ bản | gọi `POST /api/v1/users/login` |

---

## 3. Biến cấu hình (`.env`)

| Biến | Bắt buộc | Mô tả | Ví dụ |
|---|---|---|---|
| `SERVER_PORT` | ✕ | Cổng ứng dụng (mặc định `8080`) | `8080` |
| `DB_URL` | ✔ | JDBC URL tới MySQL | `jdbc:mysql://localhost:3306/user_management_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh` |
| `DB_USERNAME` | ✔ | User MySQL | `root` |
| `DB_PASSWORD` | ✔ | Mật khẩu MySQL | `your_password` |
| `JWT_SECRET` | ✔ | Secret ký JWT (HS256) – nên là chuỗi ngẫu nhiên ≥ 32 ký tự | `...` |
| `JWT_EXPIRED_ACCESS` | ✕ | Thời hạn access token (ms). Lưu ý: code hiện nhân giá trị này với 3 | `3600000` |
| `JWT_EXPIRED_REFRESH` | ✕ | Thời hạn refresh token (ms) | `2592000000` |
| `MAIL_USERNAME` | ✕ | Email gửi đi (Gmail) | `yourmail@gmail.com` |
| `MAIL_PASSWORD` | ✕ | App password Gmail 16 ký tự | `abcd efgh ijkl mnop` |
| `REDIS_HOST` | ✕ | Host Redis | `localhost` |
| `REDIS_PORT` | ✕ | Port Redis | `6379` |
| `REDIS_PASSWORD` | ✕ | Mật khẩu Redis | `...` |

> `spring.config.import: optional:file:.env[.properties]` trong `application.yaml` sẽ tự nạp file `.env` ở thư mục gốc.

---

## 4. Tài khoản test

Hệ thống **chưa seed sẵn tài khoản** (bao gồm cả admin). Mọi tài khoản được tạo qua API đăng ký sẽ có role **USER**.

### 4.1. Tạo tài khoản USER

```http
POST /api/v1/users/register
Content-Type: application/json

{
  "username": "testuser",
  "email": "testuser@example.com",
  "password": "password123",
  "fullName": "Người dùng thử nghiệm",
  "phone": "0123456789"
}
```

Sau đó đăng nhập:

```http
POST /api/v1/users/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}
```

Response trả về `accessToken` và `refreshToken`. Gắn `accessToken` vào header:

```http
Authorization: Bearer <accessToken>
```

### 4.2. Tạo tài khoản ADMIN (để dùng `/api/v1/admin/**`)

Chạy SQL sau trong MySQL (có thể thực hiện trước hoặc sau khi đăng ký):

```sql
USE user_management_db;

-- Tạo 2 role nếu chưa có
INSERT IGNORE INTO role (name, description, created_at, updated_at)
SELECT 'ADMIN', 'Quản trị viên', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM role WHERE name = 'ADMIN');

INSERT IGNORE INTO role (name, description, created_at, updated_at)
SELECT 'USER', 'Người dùng', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM role WHERE name = 'USER');

-- Gán toàn bộ permission cho ADMIN
INSERT IGNORE INTO role_permission (role_id, permission_id, created_at, updated_at)
SELECT r.id, p.id, NOW(), NOW()
FROM role r, permission p
WHERE r.name = 'ADMIN';
```

Cấp quyền ADMIN cho một user đã đăng ký (thay `<username>`):

```sql
INSERT IGNORE INTO user_role (user_id, role_id, created_at, updated_at)
SELECT u.id, r.id, NOW(), NOW()
FROM users u, role r
WHERE u.username = '<username>' AND r.name = 'ADMIN';
```

Sau khi promote, đăng nhập lại để lấy access token mới chứa authority `ROLE_ADMIN`.

> Mẹo kiểm tra quyền: gọi `GET /api/v1/auth/authorities` với access token để xem danh sách role/permission hiện có của user.

---

## 5. Tóm tắt các endpoint chính

| Nhóm | Base path | Ghi chú |
|---|---|---|
| Auth | `/api/v1/users/*`, `/api/v1/auth/*` | register, login, refresh-token, change-password, logout |
| Wallet | `/api/v1/wallets` | CRUD |
| Category | `/api/v1/categories` | CRUD + `GET /tree` |
| Transaction | `/api/v1/transactions` | CRUD + filter/pagination + `GET /export` (CSV) |
| Budget | `/api/v1/budgets` | CRUD |
| Goal | `/api/v1/goals` | CRUD + `POST /{id}/contributions` |
| Reconciliation | `/api/v1/reconciliation/wallets/{walletId}` | báo cáo đối soát |
| Admin | `/api/v1/admin/api-permissions` | quản lý mapping endpoint ↔ permission |

Chi tiết đầy đủ: xem `doc/system-design.md` hoặc Swagger UI.

---

## 6. Lưu ý

- Hibernate chạy chế độ `ddl-auto: validate` → schema phải khớp migration; nếu có lỗi, kiểm tra log Flyway.
- `JWT_SECRET` đổi môi trường thì toàn bộ token cũ hết hiệu lực.
- Endpoint `PUT /api/v1/users/send` là code phát triển (gửi OTP tới email cố định) – không dùng trong production.
- Cần Java 17; nếu dùng IDE hãy đặt Project SDK = 17 và bật Lombok annotation processing.
