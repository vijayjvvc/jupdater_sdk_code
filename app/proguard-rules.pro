# Keep your public SDK classes and methods
-keep class com.qtlws.android.jupdater.** { *; }

# If using reflection or dynamic method names
-keepclassmembers class com.qtlws.android.jupdater.** {
    *;
}

# Keep annotations if any
-keep @interface com.qtlws.android.jupdater.**

# Prevent removing the UpdateBlocking activity
-keep public class com.qtlws.android.jupdater.UpdateBlocking {
    public <init>(...);
}
