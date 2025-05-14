# App Data And Files 
## About storing App data and file
common usecases:
	• 存储用户配置、缓存、日志
	• 数据持久化（SharedPreferences, Room）
	• 管理文件系统访问（internal/external storage）
	• 遵守 Scoped Storage 的权限模型

 文档分为几大类：https://developer.android.com/training/data-storage 
	1. 🔹 Key-Value 数据（SharedPreferences / DataStore）
	2. 📁 App-specific 文件（Internal / External Storage）
	3. 📷 共享媒体文件（图片、视频、音频）
	4. 📄 使用 SAF（Storage Access Framework）打开/保存文件
	5. 🧾 数据库（Room / SQLite）

### 🔹 1️⃣ SharedPreferences（键值对存储）
这是 Android 里最轻量、最简单的本地持久化方式，非常适合保存：
	•	login status
	•	settings switches（DarkMode 开启否？）
	•	records of last time usage
	•	simple cache

 ✅ 什么是 SharedPreferences？

它是一个**键值对（Key-Value）**型的存储，适合存：Boolean, Int, Float, Long, String, Set<String>

⚠️ Not suitable for structured data, object, files or images

✅ How to use SharedPreferences？

We use PreferenceManager or Context.getSharedPreferences()。

```kotlin
val prefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
```

