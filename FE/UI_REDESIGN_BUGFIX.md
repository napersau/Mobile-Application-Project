# UI Redesign - Critical Bug Fix
**Date:** February 15, 2026  
**Time:** 21:33 - 21:40

## 🐛 Critical Bug Found & Fixed

### Issue:
After login, the app crashed immediately with:
```
android.view.InflateException: Binary XML file line #79 in com.example.fe:layout/fragment_home
Error inflating class androidx.cardview.widget.CardView
Caused by: android.content.res.Resources$NotFoundException: 
Can't find ColorStateList from drawable resource ID #0x7f070088
```

### Root Cause:
In `fragment_home.xml`, the stats CardViews were using:
```xml
app:cardBackgroundColor="@drawable/bg_stat_card"
```

**Problem:** `cardBackgroundColor` expects a **color resource**, not a drawable resource. CardView tried to load the drawable as a ColorStateList and failed.

### Solution:
Changed all stats CardViews from:
```xml
app:cardBackgroundColor="@drawable/bg_stat_card"
```

To:
```xml
app:cardBackgroundColor="@android:color/white"
```

### Files Modified:
1. ✅ `app/src/main/res/layout/fragment_home.xml` - Fixed 3 CardViews:
   - Today Words Card (line ~79)
   - Streak Days Card (line ~119)
   - Total Points Card (line ~158)

### Note:
The icon circles (TextView backgrounds) can still use `@drawable/bg_stat_card` because TextView's `android:background` attribute accepts both colors and drawables.

### Build Status:
```
BUILD SUCCESSFUL in 14s
```

### Testing:
✅ App now launches successfully after login
✅ Home page displays correctly
✅ Stats cards show with white backgrounds
✅ No more InflateException

---

## 📝 Lesson Learned

**CardView attributes that need colors (not drawables):**
- ✅ `app:cardBackgroundColor` → Use `@color/` or `@android:color/`
- ❌ Cannot use `@drawable/` for cardBackgroundColor

**View attributes that accept drawables:**
- ✅ `android:background` → Can use both `@color/` or `@drawable/`

---

## 🔍 How to Prevent This:

1. **Always use color resources for CardView backgrounds**
   ```xml
   <!-- Correct -->
   app:cardBackgroundColor="@color/white"
   app:cardBackgroundColor="@android:color/white"
   app:cardBackgroundColor="#FFFFFF"
   
   <!-- Wrong -->
   app:cardBackgroundColor="@drawable/bg_card"
   ```

2. **Use drawables for View backgrounds**
   ```xml
   <!-- Correct for any View -->
   android:background="@drawable/bg_card"
   android:background="@color/white"
   ```

3. **Check Android Studio warnings**
   - IDE usually warns about incorrect attribute types
   - Pay attention to color vs drawable requirements

---

**Status:** ✅ **FIXED**  
**Time to Fix:** 7 minutes  
**Impact:** Critical → Resolved

**App is now working correctly!** 🎉

