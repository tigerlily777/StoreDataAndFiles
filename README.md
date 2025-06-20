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
### 🔹 2️⃣ Internal storage
🔍 什么是 Internal Storage？

Internal Storage 是 应用私有的文件存储空间，其他 app 无法访问，文件也不会出现在用户的文件管理器中。

📌 特点：
	•	不需要请求权限（安全又简单）
	•	卸载 app 后文件会一起被删除
	•	文件默认只对当前 app 可见（私有）

⸻

✏️ 常见用途：
	•	缓存数据（如用户头像、草稿）
	•	存储 JSON、文本、日志等
	•	保存图片或二进制文件（如音频）

⸻

✅ 你可以使用以下方式读写文件
📥 写入文件：
```kotlin
val filename = "my_file.txt"
val fileContents = "Hello, internal storage!"
openFileOutput(filename, Context.MODE_PRIVATE).use {
    it.write(fileContents.toByteArray())
}
```
	•	openFileOutput() 会在 internal storage 中创建（或替换）这个文件
	•	MODE_PRIVATE 表示只有当前 app 可访问（默认模式）

📤 读取文件：
```kotlin
val filename = "my_file.txt"
val content = openFileInput(filename).bufferedReader().useLines { lines ->
    lines.fold("") { some, text -> "$some\n$text" }
}
```
🗂️ 文件保存在哪？

使用 context.filesDir 获取 app 的 internal storage 路径：
```kotlin
val path = applicationContext.filesDir
Log.d("Storage", "路径: $path") // 通常是 /data/data/包名/files
```
🧠 小知识：
| 模式                            | 说明                                 |
|:--------------------------------|:--------------------------------------|
| `MODE_PRIVATE`                 | 只允许当前 app 访问（默认）           |
| `MODE_APPEND`                  | 追加写入（文件存在时）                |
| `MODE_WORLD_READABLE / WRITEABLE` | 已废弃，不再推荐使用                 |


🧭 Internal Storage 图解：应用如何读写文件？

📦 内部结构图（简化视角）：
```
📱 Android 文件系统
└── /data/
    └── /data/
        └── com.example.myapp/   ← 你 app 的沙盒
            ├── files/           ← 内部存储目录
            │   ├── my_file.txt  ← 你写入的文件
            │   └── cache.json
            └── cache/           ← 临时缓存文件夹
```

📋 写入文件流程图：
```
graph TD
    A[你点击按钮/触发保存] --> B[openFileOutput("my_file.txt")]
    B --> C[返回 OutputStream]
    C --> D[write("内容".toByteArray())]
    D --> E[自动保存到 /data/data/包名/files/]
```

📋 读取文件流程图：
```
graph TD
    A[你触发读取操作] --> B[openFileInput("my_file.txt")]
    B --> C[返回 InputStream]
    C --> D[bufferedReader() → 读取内容]
    D --> E[显示在界面上]
```    
✅ 总结一句话：

内部存储 = 你 app 专属的小仓库
用 openFileOutput 写，用 openFileInput 读，数据保存在 /data/data/包名/files/ 中。

#### 一个 ✨Jetpack Compose 小项目✨，实现 内部存储的读写操作。
✅ 功能目标：
	•	输入内容，点击“保存”按钮写入内部文件
	•	点击“读取”按钮，显示文件内容

⸻

📂 文件结构
```
MainActivity.kt  ← 包含所有逻辑（Compose UI + 文件操作）
```

🧑‍💻 完整代码（Compose + 内部存储）：
```kotlin
// MainActivity.kt
package com.example.internalstorage

import android.content.Context
....


class MainActivity : ComponentActivity() {

    private val fileName = "my_internal_file.txt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InternalStorageScreen(
                context = this,
                fileName = fileName
            )
        }
    }
}

@Composable
fun InternalStorageScreen(context: Context, fileName: String) {
    var inputText by remember { mutableStateOf("") }
    var fileContent by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxSize()) {

        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("请输入内容") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row {
            Button(onClick = {
                // 写入文件
                context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
                    it.write(inputText.toByteArray())
                }
            }) {
                Text("保存")
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(onClick = {
                // 读取文件
                fileContent = try {
                    context.openFileInput(fileName).bufferedReader().use { it.readText() }
                } catch (e: Exception) {
                    "读取失败: ${e.message}"
                }
            }) {
                Text("读取")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("📄 文件内容：")
        Text(fileContent)
    }
}
```

### 🧱 Room = 3 个核心组件 - “Room = 数据类 + 接口 + 数据库类，帮我自动生成数据库操作。”

| 组件名      | 作用                                      |
|:------------|:-------------------------------------------|
| `@Entity`   | 对应一张数据库表                           |
| `@Dao`      | 定义数据的操作（增删改查）                 |
| `@Database` | 创建数据库实例，连接 Entity 和 Dao         |

图示：
```
📦 Room Database
├── 🧱 Entity (表结构)
├── 📄 DAO (数据操作)
└── 💾 Database (入口)
```

你可以类比成：
```
Entity   = Kotlin 的数据类，对应数据库的一行
DAO      = Repository 接口，定义你想查/存的动作
Database = 数据库对象，创建数据库 + 提供 DAO 实例
```

#### 🎯 Sample：Room 的最小用法预览
1️⃣ @Entity —— 表结构
```kotlin
@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: Int,
    val name: String,
    val age: Int
)
```
📌 解释：
	•	每一个 data class 是一张表
	•	@PrimaryKey 就是主键（必须有）

 
 2️⃣ @Dao —— 数据操作接口
 ```kotlin
@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    suspend fun getAll(): List<User>

    @Insert
    suspend fun insert(user: User)

    @Delete
    suspend fun delete(user: User)
}
```
📌 解释：
	•	用 @Query 写 SQL
	•	用 @Insert / @Delete 做基本操作
 
 3️⃣ @Database —— 数据库入口
 ```kotlin
@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
```
📌 解释：
	•	继承 RoomDatabase
	•	告诉 Room：我有哪几张表（entities）
	•	提供 Dao 的实例
 
 4️⃣ 初始化数据库（通常在 Repository 层或 ViewModel 里）
 ```
val db = Room.databaseBuilder(
    context,
    AppDatabase::class.java,
    "my-database"
).build()

val userDao = db.userDao()
```

🧱 Step 1：创建 @Entity 数据类

在 Room 中，每一个 @Entity 就代表数据库中的一张表。

来看一个经典示例 👇：
```
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val age: Int
)
```
| 注解 / 属性       | 说明                                               |
|:------------------|:----------------------------------------------------|
| `@Entity`         | 标注为 Room 的一张表                                 |
| `tableName`       | 设置表名，不设置则默认为类名                         |
| `@PrimaryKey`     | 设置主键，**必须有**                                 |
| `autoGenerate`    | 是否自动生成主键值（如插入新用户时自动 +1）         |
💡 这个类在 Room 中做了什么？

Room 会帮你自动生成：
	•	CREATE TABLE users (...) 的 SQL 语句
	•	每个字段对应列（Column）
	•	表结构的映射和映射器（ORM）

🧩 Step 2：创建 UserDao.kt 接口

```kotlin
import androidx.room.*

@Dao
interface UserDao {

    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM users")
    suspend fun getAll(): List<User>

    @Delete
    suspend fun delete(user: User)
}
```
✅ Step 3：创建 AppDatabase.kt  连接 Entity 和 Dao
```kotlin
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
```
