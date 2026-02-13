# Cập nhật: Chọn Loại Đề Thi Trước Khi Gọi API

## Tóm tắt thay đổi

Đã cập nhật luồng tính năng bài thi để người dùng **chọn loại đề thi trước**, sau đó mới gọi API để lấy danh sách đề thi theo loại đã chọn.

## Luồng mới (Updated Workflow)

```
Trang chủ / Tab Bài Thi
    ↓
ExamCategoryActivity / ExamsFragment (Chọn loại)
    ↓
ExamListActivity (Gọi API theo loại)
    ↓
ExamDetailActivity (Chi tiết đề thi)
    ↓
ExamTakingActivity (Làm bài)
    ↓
ExamResultActivity (Kết quả)
```

## Files đã tạo mới

### 1. ExamCategoryActivity
**File:** `app/src/main/java/com/example/fe/ui/activity/ExamCategoryActivity.kt`
**Layout:** `res/layout/activity_exam_category.xml`

Màn hình hiển thị 5 loại đề thi:
- 📄 TOEIC Full Test (200 câu, 120 phút)
- 📋 TOEIC Mini Test (50-100 câu, 30-60 phút)
- 🎓 IELTS Academic (40 câu, 180 phút)
- 📚 IELTS General (40 câu, 180 phút)
- ✏️ Mock Test (15-50 câu, 15-30 phút)

Khi click vào một loại, navigate đến ExamListActivity với `EXAM_TYPE` parameter.

### 2. ExamListActivity
**File:** `app/src/main/java/com/example/fe/ui/activity/ExamListActivity.kt`
**Layout:** `res/layout/activity_exam_list.xml`

Activity này:
- Nhận `EXAM_TYPE` từ Intent
- Gọi API `GET /api/v1/exams/type?examType={type}` 
- Hiển thị danh sách đề thi theo loại trong RecyclerView
- Sử dụng ExamAdapter đã có sẵn
- Có loading state và empty state

### 3. Drawable Resources
**File:** `res/drawable/bg_exam_icon.xml`

Background cho icon của mỗi loại đề thi (màu xanh nhạt, bo góc).

## Files đã cập nhật

### 1. HomeFragment.kt
**Thay đổi:**
```kotlin
// Trước
view.findViewById<CardView>(R.id.cardExams)?.setOnClickListener {
    // TODO: Navigate to Exams Activity
}

// Sau
view.findViewById<CardView>(R.id.cardExams)?.setOnClickListener {
    startActivity(Intent(requireContext(), ExamCategoryActivity::class.java))
}
```

### 2. ExamsFragment.kt
**Thay đổi hoàn toàn:**
- ❌ Xóa: ViewModel, RecyclerView, TabLayout logic
- ✅ Thêm: Setup category cards để navigate đến ExamListActivity

**Code mới:**
```kotlin
private fun setupCategoryCards(view: View) {
    view.findViewById<CardView>(R.id.cardToeicFull)?.setOnClickListener {
        navigateToExamList(ExamType.TOEIC_FULL_TEST)
    }
    // ... các loại khác
}

private fun navigateToExamList(examType: ExamType) {
    val intent = Intent(requireContext(), ExamListActivity::class.java)
    intent.putExtra("EXAM_TYPE", examType.name)
    startActivity(intent)
}
```

### 3. fragment_exams.xml
**Thay đổi hoàn toàn:**
- ❌ Xóa: TabLayout, RecyclerView, ProgressBar, Empty State
- ✅ Thêm: 5 CardView cho các loại đề thi (giống ExamCategoryActivity)

Layout mới hiển thị các category cards với:
- Icon emoji
- Tên loại đề thi
- Mô tả
- Thông tin thời gian và số câu

### 4. AndroidManifest.xml
**Thêm 2 activities mới:**
```xml
<activity android:name=".ui.activity.ExamCategoryActivity" 
          android:label="Loại đề thi"/>
<activity android:name=".ui.activity.ExamListActivity" 
          android:label="Danh sách đề thi"/>
```

## API Integration

### Endpoint được sử dụng
```
GET /api/v1/exams/type?examType={EXAM_TYPE}
```

**Parameters:**
- `examType`: String - Một trong các giá trị:
  - `TOEIC_FULL_TEST`
  - `TOEIC_MINI_TEST`
  - `IELTS_ACADEMIC`
  - `IELTS_GENERAL`
  - `MOCK_TEST`

**Response:**
```json
{
  "code": 1000,
  "result": [
    {
      "id": 1,
      "title": "ETS 2024 - Test 1",
      "description": "...",
      "duration": 120,
      "type": "TOEIC_FULL_TEST",
      "totalQuestions": 200,
      "questionGroups": [...]
    }
  ],
  "message": "Load exams by type successfully"
}
```

## UI/UX Improvements

### 1. Trang chủ (Home)
- Click vào card "Bài Thi" → Mở ExamCategoryActivity

### 2. Tab Bài Thi (ExamsFragment)
- Hiển thị 5 category cards
- Mỗi card có:
  - Icon đặc trưng
  - Tên loại đề thi
  - Mô tả ngắn
  - Thông tin thời gian và số câu hỏi
- Click vào một card → Mở ExamListActivity với loại đã chọn

### 3. Danh sách đề thi (ExamListActivity)
- Header hiển thị loại đề thi đã chọn
- RecyclerView hiển thị các đề thi thuộc loại đó
- Loading state khi đang tải
- Empty state nếu không có đề thi
- Click vào một đề thi → Mở ExamDetailActivity

### 4. Benefits
✅ **Không gọi API ngay lập tức**: Chỉ gọi API khi người dùng chọn loại cụ thể
✅ **Giảm load**: Không tải tất cả đề thi cùng lúc
✅ **UX tốt hơn**: Người dùng biết rõ họ đang tìm loại đề thi gì
✅ **Organized**: Phân loại rõ ràng theo ExamType enum

## Navigation Flow

```
┌──────────────┐
│  HomeFragment│
│  (Card Exams)│
└──────┬───────┘
       │ click
       ↓
┌────────────────────┐
│ ExamCategoryActivity│  ← Có thể được gọi từ trang chủ
│  (5 loại đề thi)   │
└────────┬───────────┘
         │
         ↓
┌──────────────┐
│ ExamsFragment │  ← Hoặc từ tab Bài Thi
│ (5 category  │
│   cards)     │
└──────┬───────┘
       │ click một loại
       ↓
┌──────────────────┐
│ ExamListActivity │  ← Gọi API /type?examType=...
│ (Danh sách theo │
│   loại đã chọn) │
└──────┬───────────┘
       │ click một đề thi
       ↓
┌──────────────────┐
│ExamDetailActivity│
│  (Chi tiết)     │
└──────┬───────────┘
       │ click "Bắt đầu"
       ↓
┌──────────────────┐
│ExamTakingActivity│
│  (Làm bài)      │
└──────┬───────────┘
       │ submit
       ↓
┌──────────────────┐
│ExamResultActivity│
│   (Kết quả)     │
└──────────────────┘
```

## Exam Types Chi tiết

| Type | Icon | Tên | Mô tả | Thời gian | Số câu |
|------|------|-----|-------|-----------|--------|
| TOEIC_FULL_TEST | 📄 | TOEIC Full Test | Đề thi TOEIC đầy đủ 200 câu | 120 phút | 200 |
| TOEIC_MINI_TEST | 📋 | TOEIC Mini Test | Đề thi TOEIC rút gọn 50-100 câu | 30-60 phút | 50-100 |
| IELTS_ACADEMIC | 🎓 | IELTS Academic | IELTS Học thuật - Academic | 180 phút | 40 |
| IELTS_GENERAL | 📚 | IELTS General | IELTS Tổng quát - General Training | 180 phút | 40 |
| MOCK_TEST | ✏️ | Mock Test | Bài thi thử / Kiểm tra nhanh 15 phút | 15-30 phút | 15-50 |

## Build Status

✅ **BUILD SUCCESSFUL** in 40s
- Warnings: Chỉ có deprecated warnings về `onBackPressed()` (không ảnh hưởng chức năng)

## Testing Checklist

- [ ] Từ trang chủ, click "Bài Thi" → Mở màn hình chọn loại
- [ ] Từ tab "Bài Thi" → Hiển thị 5 category cards
- [ ] Click TOEIC Full Test → Gọi API với `examType=TOEIC_FULL_TEST`
- [ ] Click TOEIC Mini Test → Gọi API với `examType=TOEIC_MINI_TEST`
- [ ] Click IELTS Academic → Gọi API với `examType=IELTS_ACADEMIC`
- [ ] Click IELTS General → Gọi API với `examType=IELTS_GENERAL`
- [ ] Click Mock Test → Gọi API với `examType=MOCK_TEST`
- [ ] Hiển thị danh sách đề thi đúng loại
- [ ] Loading state hoạt động
- [ ] Empty state khi không có đề thi
- [ ] Click vào một đề thi → Mở chi tiết
- [ ] Back button hoạt động đúng ở tất cả màn hình

## Migration Notes

### Để revert về cách cũ (load tất cả exams với TabLayout):
1. Restore `ExamsFragment.kt` và `fragment_exams.xml` từ commit trước
2. Xóa `ExamCategoryActivity.kt` và layout
3. Xóa `ExamListActivity.kt` và layout
4. Update `HomeFragment` để navigate trực tiếp đến ExamsFragment

### Để sử dụng cả hai cách:
- Giữ ExamsFragment hiển thị categories (như hiện tại)
- Thêm một "View All" button để xem tất cả đề thi
- Hoặc thêm tab "All" trong ExamListActivity

---

**Status: ✅ HOÀN THÀNH - Đã test build thành công!**

