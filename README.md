# App Data And Files 
## About storing App data and file
📦 Android 中的数据存储方式总览
| 类型                | 特点                                                         | 示例用途                             |
|:--------------------|:--------------------------------------------------------------|:-------------------------------------|
| SharedPreferences   | 键值对存储，轻量，适合小数据，如设置或登录信息               | 用户偏好设置、登录 token 等          |
| Internal Storage    | 应用私有文件（手机内部存储）                                  | 缓存文件、临时数据                   |
| External Storage    | 可公开访问的文件（如图片、下载内容），需权限                  | 下载的图片、视频、PDF                |
| Databases (Room)    | 结构化本地数据存储，支持 SQL 查询，适合大量或关系型数据        | 聊天记录、本地缓存的业务数据等       |
| Proto/DataStore     | 替代 SharedPreferences 的新方案，性能更好，支持异步           | 设置偏好、持久化状态等               |

 文档分为几大类：https://developer.android.com/training/data-storage 
* 1. 🔹 Key-Value 数据（SharedPreferences / DataStore）
* 2. 📁 App-specific 文件（Internal / External Storage）
* 3. 📷 共享媒体文件（图片、视频、音频）
* 4. 📄 使用 SAF（Storage Access Framework）打开/保存文件
* 5. 🧾 数据库（Room / SQLite）

Consider the following questions when choosing the solutions of storage:
1. How much space does your data require?
2. How reliable does data access need to be?
3. What kind of data do you need to store?
4. Should the data be private to your app?

### Save to app-specific storage
Access app-specific files 


### 🔹 1️⃣ SharedPreferences（键值对存储）
这是 Android most lightweighted、最简单的本地持久化方式，非常适合保存：
	•	login status
	•	settings switches（DarkMode 开启否？）
	•	records of last time usage
	•	simple cache

 ✅ What is SharedPreferences？

它是一个**键值对（Key-Value）**型的存储，suitable for：Boolean, Int, Float, Long, String, Set<String>

⚠️ Not suitable for structured data, object, files or images

✅ How to use SharedPreferences？

We use PreferenceManager or Context.getSharedPreferences()。

```kotlin
val prefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
```
✅ 写入数据（edit + apply 或 commit）
```kotlin
prefs.edit().putString("username", "Alice").apply()
```
* apply() 异步（推荐）
* commit() 同步（立即写入，可能卡顿）

✅ 读取数据
```
val username = prefs.getString("username", "default_value")
```
✅ 删除或清除数据
```
prefs.edit().remove("username").apply()
prefs.edit().clear().apply() // 清空所有
```
✅ 存储位置？
/data/data/your.package.name/shared_prefs/my_prefs.xml
只能被你这个 App 访问，非常安全（默认 Internal Storage）。

🌟 常见使用场景
| 场景                 | 用法                                 |
|:----------------------|:--------------------------------------|
| 保存上次登录的用户名 | `putString("last_username", "...")`   |
| 暗黑模式是否启用     | `putBoolean("dark_mode", true)`       |
| 用户是否已登录       | `putBoolean("is_logged_in", true)`    |

⸻

🎯 一个简单实用的 SharedPreferences 小练习

我们做一个简单的登录状态存储器：
✨ 功能说明
	•	输入用户名
	•	点击「保存用户名」后写入 SharedPreferences
	•	点击「读取用户名」后显示读取结果
 
 MainActivity.kt：
 ```kotlin
package com.example.sharedprefscompose

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    companion object {
        const val PREFS_NAME = "my_prefs"
        const val KEY_USERNAME = "username"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UserPreferenceScreen()
        }
    }
}

@Composable
fun UserPreferenceScreen() {
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE)

    var inputText by remember { mutableStateOf(TextFieldValue("")) }
    var displayText by remember { mutableStateOf("display username") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Enter username") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                sharedPrefs.edit()
                    .putString(MainActivity.KEY_USERNAME, inputText.text)
                    .apply()
                displayText = "Username saved ✅"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save username")
        }

        Button(
            onClick = {
                val name = sharedPrefs.getString(MainActivity.KEY_USERNAME, "默认用户名")
                displayText = "current username：$name"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("read username")
        }

        Text(
            text = displayText,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}
```

 
