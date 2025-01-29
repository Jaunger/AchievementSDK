# AchievementsSDK

## ✨ Introduction
AchievementsSDK is a user-friendly library that helps Android developers seamlessly integrate achievements into their applications. It offers:
- A simple, API-based approach to create and manage achievements.
- Automatic user progress tracking.
- A built-in UI for displaying achievements.
- Quick and straightforward setup.

---

## 🗒️ How to Use

### 1️⃣ Create an Achievement List 📝
1. Visit [our website](https://achievementapi.onrender.com) to create your achievement list.
2. After creation, you'll receive an **API key** (you'll use this in **Step 4**).

---

### 2️⃣ Add the JitPack Repository 🏗️
In your **root** `build.gradle` file:

~~~~groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
~~~~

---

### 3️⃣ Add the Dependency 📦
In your **module-level** `build.gradle` file, add **AchievementsSDK**:

~~~~groovy
dependencies {
    implementation 'com.github.Jaunger:AchievementSDK:1.0.0'
}
~~~~

---

### 4️⃣ Provide Your API Key 🔑
Open your `AndroidManifest.xml` and add the meta-data with your API key:

~~~~xml
<application
    ...>
    <meta-data
        android:name="com.achievementsLibrary.API_KEY"
        android:value="YOUR_API_KEY_HERE" />
</application>
~~~~

Replace `YOUR_API_KEY_HERE` with the API key from your achievement list.

---

### 5️⃣ Initialize the SDK ⚙️
In your `Application` class or the main `Activity`, initialize the SDK:

~~~~java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    AchievementsSDK.getInstance().init(
        this,
        new AchievementsSDK.InitCallback() {
            @Override
            public void onSuccess() {
                Log.d("AchievementsSDK", "SDK initialized successfully.");
            }

            @Override
            public void onFailure(String error) {
                Log.e("AchievementsSDK", "SDK initialization failed: " + error);
            }
        }
    );
}
~~~~

---

### 6️⃣ Create or Fetch a Player 👤
Use `PlayerManager` to ensure the player is created or fetched before updating achievements:

~~~~java
String rawUsername = "player123"; // Replace with your player's unique identifier

PlayerManager.createOrFetchPlayer(
    rawUsername,
    new PlayerManager.PlayerCallback() {
        @Override
        public void onSuccess(String response) {
            Log.d("PlayerManager", "Player created or fetched: " + response);
        }

        @Override
        public void onError(String error) {
            Log.e("PlayerManager", "Error creating or fetching player: " + error);
        }
    }
);
~~~~

---

### 7️⃣ Display Achievements Dialog 🏆
Show the achievements dialog (completed and in-progress):

~~~~java
Button btnShowAchievements = findViewById(R.id.btnShowAchievements);
btnShowAchievements.setOnClickListener(v -> {
    AchievementsSDK sdk = AchievementsSDK.getInstance();

    if (!sdk.isInitialized()) {
        Log.e("AchievementsSDK", "SDK not initialized. Please call init() first.");
        return;
    }

    DialogFragment achievementsDialog = sdk.getAchievementsDialogFragment();
    achievementsDialog.show(getSupportFragmentManager(), "AchievementsDialog");
});
~~~~

---

### 8️⃣ Update Achievement Progress 📈
Track the player's progress for a specific achievement:

~~~~java
String rawUsername = "player123";          // The player's username
String achievementId = "achievementId123"; // The achievement's ID
int progressDelta = 10;                    // Amount of progress to add

PlayerManager.updateAchievementProgress(
    rawUsername,
    achievementId,
    progressDelta,
    new PlayerManager.PlayerCallback() {
        @Override
        public void onSuccess(String response) {
            Log.d("AchievementUpdate", "Progress updated: " + response);
        }

        @Override
        public void onError(String error) {
            Log.e("AchievementUpdate", "Failed to update progress: " + error);
        }
    }
);
~~~~

**Notes:**
- `rawUsername` must match the one used when creating/fetching the player.
- `achievementId` must be the ID of an existing achievement.
- Adjust `progressDelta` based on how much progress you want to record.
