---
name: Lexicon Editorial
colors:
  surface: '#f9f9f9'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f3f3f3'
  surface-container: '#eeeeee'
  surface-container-high: '#e8e8e8'
  on-surface: '#1a1c1c'
  on-surface-variant: '#4c4546'
  outline: '#7e7576'
  outline-variant: '#cfc4c5'
  primary: '#000000'
  on-primary: '#ffffff'
  secondary: '#a33800'
  on-secondary: '#ffffff'
  error: '#ba1a1a'
  error-container: '#ffdad6'
  border-subtle: '#e5e5e5'
  text-muted: '#666666'
typography:
  display:
    fontFamily: Source Serif 4
    fontSize: 48px
    fontWeight: '700'
    lineHeight: '1.1'
  heading:
    fontFamily: Source Serif 4
    fontSize: 24px
    fontWeight: '600'
    lineHeight: '1.3'
  body:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: '1.5'
  metadata:
    fontFamily: JetBrains Mono
    fontSize: 12px
    fontWeight: '500'
    lineHeight: '1.2'
    letterSpacing: 0.05em
spacing:
  unit: 4px
  gutter: 24px
  margin-mobile: 16px
  margin-desktop: 64px
  max-width: 1280px
---

## Brand and Style

Lexicon Editorial kết hợp cảm giác xuất bản cổ điển với tiện ích số hiện đại. Hệ thống ưu tiên độ dễ đọc, nền đơn sắc có tương phản cao, khoảng trắng có chủ đích và chi tiết kỹ thuật chính xác.

## Color

Nền trắng và xám trung tính mô phỏng giấy in. Đen là màu cấu trúc và hành động chính; cam `#a33800` chỉ dùng cho hành động kích hoạt, cảnh báo hoặc điểm nhấn có ý nghĩa. Trạng thái không được truyền đạt chỉ bằng màu.

## Typography

- `Source Serif 4`: tiêu đề và nội dung mang tính biên tập.
- `Inter`: nội dung và điều khiển giao diện.
- `JetBrains Mono`: mã, ngày, danh mục và metadata.

Độ dài đoạn văn tối đa 70 ký tự; tiêu đề dùng `text-wrap: balance`, nội dung dài dùng `text-wrap: pretty`.

## Layout and Spacing

Desktop dùng lưới 12 cột, gutter 24px và chiều rộng tối đa 1280px. Thành phần nội bộ có mật độ vừa phải nhưng các nhóm nội dung được ngăn bằng khoảng trắng và đường kẻ. Responsive thay đổi cấu trúc thay vì chỉ thu nhỏ chữ.

## Components

- Góc vuông; avatar hoặc chỉ báo tròn là ngoại lệ có chức năng.
- Primary button nền đen, chữ trắng; hover dùng cam.
- Secondary button nền trắng, viền đen 1px.
- Input có viền 1px, label luôn hiển thị và focus rõ.
- Danh sách và bảng dùng đường phân cách mảnh, header đậm và metadata monospace.
- Không dùng shadow để tạo cấu trúc; modal/dropdown dùng nền đặc và viền 2px.

## Motion and Accessibility

Chuyển trạng thái trong 150–250ms, không tạo chuỗi animation khi tải trang. Mọi chuyển động có phương án `prefers-reduced-motion`. Tất cả control có trạng thái hover, focus, active, disabled và error.
