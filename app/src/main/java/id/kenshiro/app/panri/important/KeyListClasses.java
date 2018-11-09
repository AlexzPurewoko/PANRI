package id.kenshiro.app.panri.important;

public class KeyListClasses {
    public static final int APP_IS_OLDER_VERSION = 0xfab;
    public static final int APP_IS_SAME_VERSION = 0xfaf;
    public static final int APP_IS_NEWER_VERSION = 0xf44;
    public static final int APP_IS_FIRST_USAGE = 0xfaa;
    public static final int DB_IS_FIRST_USAGE = 0xaac;
    public static final int DB_REQUEST_UPDATE = 0xaffc;
    public static final int DB_IS_NEWER_VERSION = 0xaab;
    public static final int DB_IS_OLDER_IN_APP_VERSION = 0xaaf;
    public static final String KEY_SHARED_DATA_CURRENT_IMG_NAVHEADER = "key_nav";
    public static final String SHARED_PREF_NAME = "panri_data";
    public static final int DB_IS_SAME_VERSION = 0xaca;
    public static final String APP_CONDITION_KEY = "APP_CONDITION_KEY_EXTRAS";
    public static final String DB_CONDITION_KEY = "DB_CONDITION_KEY_EXTRAS";
    public static final String LIST_PENYAKIT_CIRI_KEY_CACHE = "key_ciri_data_penyakit";
    public static final String KEY_AUTOCHECKUPDATE_APPDATA = "enable_autocheck_update";

    // for identification of available updates DB
    public static final int UPDATE_DB_NOT_AVAILABLE_INTERNET_MISSING = 0x66;
    public static final int UPDATE_DB_IS_AVAILABLE = 0x6A;
    public static final int UPDATE_DB_NOT_AVAILABLE = 0x6b;

    // FOR DATA VERSION
    public static final String KEY_DATA_LIBRARY_VERSION = "data_library_version";
    public static final String KEY_IKLAN_VERSION = "data_iklan_version";
    public static final String KEY_APP_VERSION = "app_version";
    public static final String KEY_LIST_VERSION_DB = "DB_LIST_VERSION_EXTRAS";
}