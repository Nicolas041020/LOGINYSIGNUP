package co.edu.unipiloto.labloginsignup;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.Manifest;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SignUp extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private EditText etDireccion,contra,confirm_passw,mail,nombre,username;
    private Button btnRegistrar;
    private TextInputEditText fechaNacimientoInput;
    private Spinner spinner;

    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        nombre = findViewById(R.id.fullname);
        username = findViewById(R.id.username);
        fechaNacimientoInput = findViewById(R.id.FechaNacimiento);
        etDireccion = findViewById(R.id.address);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        btnRegistrar = findViewById(R.id.register);
        contra = findViewById(R.id.passw);
        confirm_passw = findViewById(R.id.confir_passw);
        mail = findViewById(R.id.email);
        spinner=findViewById(R.id.spinnerRol);
        radioGroup = findViewById(R.id.radio);


        fechaNacimientoInput.setOnClickListener(v -> mostrarDatePicker());



        obtenerUbi();
        btnRegistrar.setOnClickListener(v -> {
        if (validarAll()){
            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUp.this, FirstPage.class);
                startActivity(intent);
        }
        });
    }

    public boolean validarAll(){

        boolean nom,use,mai,spi;
        String mailStr = mail.getText().toString();
        nom = nombre.getText().toString().isEmpty();
        use = username.getText().toString().isEmpty();
        mai = !Patterns.EMAIL_ADDRESS.matcher(mailStr).matches();
        spi = spinner.getSelectedItem().toString().equals("Seleccione su rol");
        if (nom) nombre.setError("DEBE LLENAR ESTE CAMPO");
        if (use) username.setError("DEBE LLENAR ESTE CAMPO");
        if (mai) mail.setError("Un correo valido debe contener nombre, dominio y no contener caracteres invalidos");
        if(radioGroup.getCheckedRadioButtonId() == -1 || spi) Toast.makeText(this,"DEBE SELECCIONAR UNA OPCION", Toast.LENGTH_SHORT).show();
        if (confirmPassw()==0){
            Toast.makeText(this, "Las contraseñas deben coincidir", Toast.LENGTH_SHORT).show();
        } else if (confirmPassw()==2) {
            contra.setError("DEBE LLENAR ESTE CAMPO");
            confirm_passw.setError("DEBE LLENAR ESTE CAMPO");
        }else return validarFecha() && !nom && !use && !mai && !spi && confirmPassw() == 1 && radioGroup.getCheckedRadioButtonId() != -1;
        return false;
    }
    public int confirmPassw(){
        String contraStr = contra.getText().toString();
        String confPasswStr = confirm_passw.getText().toString();
        if (!(contraStr.isEmpty() && confPasswStr.isEmpty())){
             if (contraStr.equals(confPasswStr)) {
                 return 1;
             }else{
                 return 0;
             }
        }
        return 2;
    }


    private void mostrarDatePicker() {
        final Calendar calendario = Calendar.getInstance();
        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String fechaSeleccionada = dayOfMonth + "/" + (month + 1) + "/" + year;
            fechaNacimientoInput.setText(fechaSeleccionada);
        }, anio, mes, dia);

        calendario.add(Calendar.YEAR, -18);
        datePickerDialog.getDatePicker().setMaxDate(calendario.getTimeInMillis());

        datePickerDialog.show();
    }

    private boolean validarFecha() {
        String fechaSeleccionada = fechaNacimientoInput.getText().toString();
        if (fechaSeleccionada.isEmpty()) {
            fechaNacimientoInput.setError("Debe seleccionar una fecha");
            return false;
        }
        fechaNacimientoInput.setError(null);
        return true;
    }
    private void obtenerUbi() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                obtenerDireccion(location.getLatitude(), location.getLongitude());
            }
        });
    }

    private void obtenerDireccion(double lat, double lon) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> direcciones = geocoder.getFromLocation(lat, lon, 1);
            if (!direcciones.isEmpty()) {
                Address direccion = direcciones.get(0);
                etDireccion.setText(direccion.getAddressLine(0));
            }
        } catch (IOException e) {
            e.printStackTrace();
            etDireccion.setText("No se pudo obtener la dirección");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            obtenerUbi();
        }
    }
}