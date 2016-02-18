import dao.Dao;
import dao.DaoException;
import dao.XmlDaoFactory;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Singleton-class for storing setting of the application
 */
public class Settings {

    /**
     * The of name of the resource file with application settings
     */
    private final String SETTINGS_FILE = "settings.xml";

    /**
     * Available settings of the application
     */
    public enum Setting {

        MAIN_RESOURCE_NAME {
            @Override
            public String getName() {
                return "resource-name";
            }

            @Override
            public Object getDefaultValue() {
                return "tasks_db1.xml";
            }
        };

        /**
         * @return name of the settings item
         */
        public abstract String getName();

        /**
         * @return default value of the settings item
         */
        public abstract Object getDefaultValue();
    }

    /**
     * Singleton instance
     */
    private static Settings instance = new Settings();

    private Dao daoSettings = null;
    List<SettingsItem> settings = null;

    private Settings() {

        try {
            daoSettings = new XmlDaoFactory().createDao(SETTINGS_FILE, SettingsItem.class);
            settings = daoSettings.getAll();
        } catch (DaoException e) {
            throw new RuntimeException("Settings loading error", e);
        }
    }

    public String getSettingValue(Setting setting) {

        for (SettingsItem settingsItem : settings) {
            if (settingsItem.getName().equals(setting.getName())) {
                return (String) settingsItem.getValue();
            }
        }

        throw new RuntimeException("Setting not found");
    }

    public void setSettingValue(Setting setting, Object settingValue) {

        for (int i = 0; i < settings.size(); i++) {
            if (settings.get(i).getName().equals(setting.getName())) {
                settings.set(i, new SettingsItem<>(setting.getName(), settingValue));
            }
        }

        writeSettings();
    }

    public void setDefault(Setting setting) {

        for (int i = 0; i < settings.size(); i++) {
            if (settings.get(i).getName().equals(setting.getName())) {
                settings.set(i, new SettingsItem<>(setting.getName(), setting.getDefaultValue()));
            }
        }

        writeSettings();
    }

    /**
     * Writes settings to the settings file
     */
    private void writeSettings() {

        for (int i = 0; i < settings.size(); i++) {
            try {
                daoSettings.update(i, settings.get(i));
            } catch (DaoException e) {
                throw new RuntimeException("Error in writing settings", e);
            }
        }
    }

    public static Settings getInstance() {
        return instance;
    }

    @XmlRootElement(name = "setting")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class SettingsItem<T> {

        @XmlAttribute
        private String name;

        @XmlElement
        private T value;

        public SettingsItem() {

        }

        public SettingsItem(String name, T value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "SettingsItem {" +
                    "name='" + name + '\'' +
                    ", value=" + value.getClass().getName() +
                    '}';
        }
    }
}