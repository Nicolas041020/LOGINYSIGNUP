package co.edu.unipiloto.labloginsignup;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.Manifest;
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
    private EditText etDireccion;
    private Button btnRegistrar;
    private TextInputEditText fechaNacimientoInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        fechaNacimientoInput = findViewById(R.id.FechaNacimiento);
        etDireccion = findViewById(R.id.address);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        btnRegistrar = findViewById(R.id.register);

        fechaNacimientoInput.setOnClickListener(v -> mostrarDatePicker());

        obtenerUbi();
        btnRegistrar.setOnClickListener(v -> {
            if (validarFecha()) {
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUp.this, FirstPage.class);
                startActivity(intent);
            }
        });
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