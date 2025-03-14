package co.edu.unipiloto.labloginsignup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LogIn extends AppCompatActivity {


    private EditText username, password;
    private Button btnAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);
        username = findViewById(R.id.usernameLI);
        password = findViewById(R.id.passwordLI);
        btnAccess = findViewById(R.id.logInLI);

        btnAccess.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                review();

            }
        });

    }

    /*
    public void startLogIn(View view){
        access();
        Intent intent = new Intent(this,Home.class);
        startActivity(intent);
    }
     */
    public void review(){
        boolean loginIsValid = true;

        String usernameStr = username.getText().toString().trim();
        String passwordStr = password.getText().toString().trim();

        if(usernameStr.isEmpty() || passwordStr.isEmpty()){
            loginIsValid=false;
            Toast.makeText(this, "Bueno pero ponga algo", Toast.LENGTH_SHORT).show();
        }

        if(loginIsValid) {
            Toast.makeText(this, "Lleno todos los campos correctamente", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LogIn.this,FirstPage.class);
            startActivity(intent);
        }
        else Toast.makeText(this, "Pero llene bien todos los campos", Toast.LENGTH_SHORT).show();
    }

}