package settings;

import dao.Dao;
import dao.exceptions.DaoException;
import dao.factories.SettingsDaoFactory;
import settings.exceptions.SettingsException;
import views.SimpleConsoleTaskView;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton-class for storing setting of the application
 */
public class Settings {

    /**
     * The of name of the resource file with application settings
     */
    public final static String SETTINGS_FILE = "config.cfg";

    /**
     * Available settings of the application
     */
    public enum Setting {

        MAIN_RESOURCE_NAME {
            @Override
            public Object getDefaultValue() {
                return "tasks_db.xml";
            }
        },

        WELCOME_SYMBOL {
            @Override
            public Object getDefaultValue() {
                return "> ";
            }
        },

        WELCOME_MESSAGE {
            @Override
            public Object getDefaultValue() {
                return "Task Manager\ntype 'help' to get help, 'exit' to exit the program";
            }
        },

        DATETIME_FORMAT {
            @Override
            public Object getDefaultValue() {
                return "dd.mm.yyyy HH:mm";
            }
        },

        CONSOLE_VIEW_LENGTH {
            @Override
            public Object getDefaultValue() {
                return 48;
            }
        },

        TASK_VIEW {
            @Override
            public Object getDefaultValue() {
                return SimpleConsoleTaskView.class.getName();
            }
        };

        /**
         * @return default value of the settings item
         */
        public abstract Object getDefaultValue();
    }

    /**
     * Singleton instance
     */
    private static Settings instance = null;

    private Dao daoSettings = null;
    List<SettingsItem> settings = null;

    private Settings() throws SettingsException {

        try {
            daoSettings = new SettingsDaoFactory().createDao();
            settings = daoSettings.getAll();
        } catch (DaoException e) {
            throw new SettingsException("Settings loading error", e);
        }
    }

    public Object getSettingValue(Setting setting) {

        for (SettingsItem settingsItem : settings) {
            if (settingsItem.getName().equals(setting.toString())) {
                return settingsItem.getValue();
            }
        }

        throw new RuntimeException("Setting not found");
    }

    public void setSettingValue(Setting setting, Object settingValue) {

        for (int i = 0; i < settings.size(); i++) {
            if (settings.get(i).getName().equals(setting.toString())) {
                settings.set(i, new SettingsItem<>(setting.toString(), settingValue));
            }
        }

        writeSettings();
    }

    public void setDefaultSettings() {

        List<SettingsItem> defaultSettings = new ArrayList<>();

        for (Setting setting : Setting.values()) {
            defaultSettings.add(new SettingsItem(setting.toString(), setting.getDefaultValue()));
        }

        settings = new ArrayList<>(defaultSettings);
        writeSettings();
    }

    /**
     * Writes settings to the settings file
     */
    private void writeSettings() {

        try {
            daoSettings.updateAll(settings);
        } catch (DaoException e) {
            throw new RuntimeException("Error in writing settings", e);
        }
    }

    public static synchronized Settings getInstance() throws SettingsException {

        if (instance == null) {
            instance = new Settings();
        }

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
    }
}
