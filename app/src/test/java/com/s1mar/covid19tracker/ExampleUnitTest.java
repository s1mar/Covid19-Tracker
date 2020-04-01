package com.s1mar.covid19tracker;

import android.util.Log;

import com.s1mar.covid19tracker.data.FireUsers;
import com.s1mar.covid19tracker.data.models.MUser;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private static final String TAG = ExampleUnitTest.class.getSimpleName();
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void addUser() {
        FireUsers.addUser("Oliver Queen", "greenArrow", true, result -> {
            Log.d(TAG, "addUser: " + result);
        });
    }
    @Test
    public void getUser() {
        FireUsers.getUser("Oliver Queen",result -> {
            Log.d(TAG, "getUser: "+result);
        });
    }
    @Test
    public void updateUser(){
        MUser employee = new MUser();
        employee.setUsername("Oliver Queen");
        employee.setAdmin(false);
        FireUsers.updateUser(employee,result -> {
            Log.d(TAG, "updateUser: ");
        });
    }
    @Test
    public void deleteUser(){
        FireUsers.deleteUser("Oliver Queen",result -> {
            Log.d(TAG, "deleteUser: "+result);
        });
    }
}
