import dao.Dao;
import dao.DaoException;
import dao.XmlDaoFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TaskModelTest {

    private static Dao dao;

    private static TaskModel[] testTasks;

    @BeforeClass
    public static void init() throws DaoException {

        dao = new XmlDaoFactory().createDao("tasks_db1.xml", TaskModel.class);
        TaskModel.setDao(dao);

        testTasks = new TaskModel[]{
                new TaskModel(0, "NetCracker", "Complete the first task", new Date()),
                new TaskModel(1, "SSU", "Data bases - create GUI", new Date()),
                new TaskModel(2, "SSU", "AI - complete the second theme", new Date())
        };
    }

    @Before
    public void prepareFile() throws DaoException {
        // Clean the file
        dao.removeAll();

        // And write objects again
        for (TaskModel taskModel : testTasks) {
            dao.add(taskModel);
        }
    }

    @Test
    public void testGetModel() throws TaskException {

        for (int i = 0; i < testTasks.length; i++) {
            assertEquals(testTasks[i], TaskModel.getModel(i));
        }
    }

    @Test
    public void testGetAllModels() throws TaskException {

        List<TaskModel> list = TaskModel.getAllModels();

        for (int i = 0; i < list.size(); i++) {
            assertEquals(testTasks[i], list.get(i));
        }
    }
}
