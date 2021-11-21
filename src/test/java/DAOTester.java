import com.revature.annotations.Column;
import com.revature.annotations.NotNull;
import com.revature.annotations.PrimaryKey;
import com.revature.annotations.Unique;
import com.revature.services.ORM;
import org.junit.jupiter.api.Test;

public class DAOTester {
    @Test
    public static void testCreatingTableFromObject(){
        class Student{
            @PrimaryKey(isSerial = true)
            private String username;
            @NotNull
            private String password;
            @Column
            private String firstName;
            @Column
            int age;
            @Unique
            private int id;
        }
    }
}
