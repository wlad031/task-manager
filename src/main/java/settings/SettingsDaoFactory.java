package settings;

import dao.Dao;
import dao.DaoException;
import dao.ResourceDaoFactory;
import dao.XmlDao;

public class SettingsDaoFactory implements ResourceDaoFactory {

    @Override
    public Dao createDao() throws DaoException, SettingsException {
        return new XmlDao(Settings.SETTINGS_FILE, Settings.SettingsItem.class);
    }
}
