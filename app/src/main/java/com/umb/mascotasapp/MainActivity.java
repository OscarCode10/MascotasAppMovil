package com.umb.mascotasapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * MascotasApp — CRUD de mascotas de una veterinaria.
 * Consume servicios PHP (XAMPP: Apache + MySQL) usando la biblioteca Volley.
 *
 *  C -> insertar.php   R -> listar.php   U -> actualizar.php   D -> eliminar.php
 */
public class MainActivity extends AppCompatActivity {

    // ================== CONFIGURACIÓN DEL SERVIDOR ==================
    // Emulador de Android Studio: 10.0.2.2 = "localhost" del PC donde corre XAMPP.
    // Celular físico (misma red WiFi): cambiar por la IP del PC, ej. "http://192.168.1.10/MascotasDB/"
    // private static final String BASE_URL = "http://10.0.2.2:8080/MascotasDB/";   // emulador
    private static final String BASE_URL = "http://192.168.1.8:8080/MascotasDB/";   // celular físico
    // =================================================================

    // Datos de la lista desplegable y su imagen correspondiente
    private final String[] especies = {"Perro", "Gato", "Ave", "Conejo", "Pez"};
    private final int[] imagenes = {
            R.drawable.ic_perro, R.drawable.ic_gato, R.drawable.ic_ave,
            R.drawable.ic_conejo, R.drawable.ic_pez
    };

    private EditText etNombre, etEdad;
    private Spinner spEspecie;
    private ImageView ivEspecie;
    private RadioGroup rgSexo;
    private RadioButton rbMacho, rbHembra;
    private CheckBox cbVacunado, cbEsterilizado;
    private Button btnGuardar, btnActualizar, btnEliminar;
    private TextView tvModo, tvVacio;
    private LinearLayout contenedorLista;
    private ProgressBar progreso;

    private RequestQueue cola;          // Cola de peticiones de Volley
    private int idSeleccionado = -1;    // -1 = modo "nuevo"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNombre        = findViewById(R.id.etNombre);
        etEdad          = findViewById(R.id.etEdad);
        spEspecie       = findViewById(R.id.spEspecie);
        ivEspecie       = findViewById(R.id.ivEspecie);
        rgSexo          = findViewById(R.id.rgSexo);
        rbMacho         = findViewById(R.id.rbMacho);
        rbHembra        = findViewById(R.id.rbHembra);
        cbVacunado      = findViewById(R.id.cbVacunado);
        cbEsterilizado  = findViewById(R.id.cbEsterilizado);
        btnGuardar      = findViewById(R.id.btnGuardar);
        btnActualizar   = findViewById(R.id.btnActualizar);
        btnEliminar     = findViewById(R.id.btnEliminar);
        tvModo          = findViewById(R.id.tvModo);
        tvVacio         = findViewById(R.id.tvVacio);
        contenedorLista = findViewById(R.id.contenedorLista);
        progreso        = findViewById(R.id.progreso);

        cola = Volley.newRequestQueue(this);

        // 1. Lista desplegable (Spinner)
        ArrayAdapter<String> adaptador = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, especies);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEspecie.setAdapter(adaptador);

        // 4. El ImageView cambia según la especie elegida
        spEspecie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                ivEspecie.setImageResource(imagenes[pos]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        btnGuardar.setOnClickListener(v -> guardar());
        btnActualizar.setOnClickListener(v -> actualizar());
        btnEliminar.setOnClickListener(v -> confirmarEliminar());
        findViewById(R.id.btnLimpiar).setOnClickListener(v -> limpiar());
        findViewById(R.id.btnRefrescar).setOnClickListener(v -> listar());

        listar();
    }

    // ===================== CREATE =====================
    private void guardar() {
        Map<String, String> datos = leerFormulario();
        if (datos == null) return;
        enviarPost("insertar.php", datos);
    }

    // ===================== READ =====================
    private void listar() {
        progreso.setVisibility(View.VISIBLE);
        StringRequest peticion = new StringRequest(Request.Method.GET, BASE_URL + "listar.php",
                respuesta -> {
                    progreso.setVisibility(View.GONE);
                    try {
                        JSONObject json = new JSONObject(respuesta);
                        if (!json.getBoolean("ok")) {
                            toast(json.getString("mensaje"));
                            return;
                        }
                        pintarLista(json.getJSONArray("data"));
                    } catch (Exception e) {
                        toast("Respuesta inválida del servidor: " + e.getMessage());
                    }
                },
                error -> {
                    progreso.setVisibility(View.GONE);
                    errorConexion(error);
                });
        configurar(peticion);
        cola.add(peticion);
    }

    // ===================== UPDATE =====================
    private void actualizar() {
        if (idSeleccionado < 0) return;
        Map<String, String> datos = leerFormulario();
        if (datos == null) return;
        datos.put("id", String.valueOf(idSeleccionado));
        enviarPost("actualizar.php", datos);
    }

    // ===================== DELETE =====================
    private void confirmarEliminar() {
        if (idSeleccionado < 0) return;
        new AlertDialog.Builder(this)
                .setTitle("Eliminar mascota")
                .setMessage("¿Seguro que deseas eliminar a " + etNombre.getText().toString().trim() + "?")
                .setPositiveButton("Eliminar", (d, w) -> {
                    Map<String, String> datos = new HashMap<>();
                    datos.put("id", String.valueOf(idSeleccionado));
                    enviarPost("eliminar.php", datos);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // ---------- Petición POST genérica con Volley (insertar / actualizar / eliminar) ----------
    private void enviarPost(String servicio, Map<String, String> datos) {
        progreso.setVisibility(View.VISIBLE);
        StringRequest peticion = new StringRequest(Request.Method.POST, BASE_URL + servicio,
                respuesta -> {
                    progreso.setVisibility(View.GONE);
                    try {
                        JSONObject json = new JSONObject(respuesta);
                        toast(json.getString("mensaje"));
                        if (json.getBoolean("ok")) {
                            limpiar();
                            listar();
                        }
                    } catch (Exception e) {
                        toast("Respuesta inválida del servidor: " + respuesta);
                    }
                },
                error -> {
                    progreso.setVisibility(View.GONE);
                    errorConexion(error);
                }) {
            @Override
            protected Map<String, String> getParams() {
                return datos;   // Volley los envía como formulario (x-www-form-urlencoded) -> $_POST en PHP
            }
        };
        configurar(peticion);
        cola.add(peticion);
    }

    // Lee y valida el formulario. Devuelve null si algo falta.
    private Map<String, String> leerFormulario() {
        String nombre = etNombre.getText().toString().trim();
        String edad = etEdad.getText().toString().trim();

        if (nombre.isEmpty()) {
            etNombre.setError("Escribe el nombre");
            etNombre.requestFocus();
            return null;
        }
        if (edad.isEmpty()) edad = "0";

        Map<String, String> datos = new HashMap<>();
        datos.put("nombre", nombre);
        datos.put("edad", edad);
        datos.put("especie", spEspecie.getSelectedItem().toString());                              // Spinner
        datos.put("sexo", rgSexo.getCheckedRadioButtonId() == R.id.rbHembra ? "Hembra" : "Macho"); // RadioButton
        datos.put("vacunado", cbVacunado.isChecked() ? "1" : "0");                                 // CheckBox
        datos.put("esterilizado", cbEsterilizado.isChecked() ? "1" : "0");                         // CheckBox
        return datos;
    }

    // Dibuja cada mascota como una tarjeta dentro del contenedor
    private void pintarLista(JSONArray lista) throws Exception {
        contenedorLista.removeAllViews();
        tvVacio.setVisibility(lista.length() == 0 ? View.VISIBLE : View.GONE);
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < lista.length(); i++) {
            JSONObject m = lista.getJSONObject(i);
            View item = inflater.inflate(R.layout.item_mascota, contenedorLista, false);

            String especie = m.getString("especie");
            ((ImageView) item.findViewById(R.id.ivItem)).setImageResource(imagenDe(especie));
            ((TextView) item.findViewById(R.id.tvItemNombre)).setText(m.getString("nombre"));
            ((TextView) item.findViewById(R.id.tvItemDetalle)).setText(
                    especie + " · " + m.getString("sexo") + " · " + m.getInt("edad") + " año(s)");

            String salud = (m.getInt("vacunado") == 1 ? "✔ Vacunado" : "✘ Sin vacunas")
                    + "   " + (m.getInt("esterilizado") == 1 ? "✔ Esterilizado" : "✘ No esterilizado");
            ((TextView) item.findViewById(R.id.tvItemSalud)).setText(salud);

            item.setOnClickListener(v -> cargarEnFormulario(m));
            contenedorLista.addView(item);
        }
    }

    // Al tocar una mascota se carga en el formulario para editar/eliminar
    private void cargarEnFormulario(JSONObject m) {
        try {
            idSeleccionado = m.getInt("id");
            etNombre.setText(m.getString("nombre"));
            etEdad.setText(String.valueOf(m.getInt("edad")));
            spEspecie.setSelection(posicionDe(m.getString("especie")));
            if ("Hembra".equals(m.getString("sexo"))) rbHembra.setChecked(true); else rbMacho.setChecked(true);
            cbVacunado.setChecked(m.getInt("vacunado") == 1);
            cbEsterilizado.setChecked(m.getInt("esterilizado") == 1);

            tvModo.setText("Editando: " + m.getString("nombre") + " (id " + idSeleccionado + ")");
            btnGuardar.setEnabled(false);
            btnActualizar.setEnabled(true);
            btnEliminar.setEnabled(true);
            ((ScrollView) findViewById(R.id.scroll)).smoothScrollTo(0, 0);   // sube al formulario
        } catch (Exception e) {
            toast("Error cargando la mascota");
        }
    }

    private void limpiar() {
        idSeleccionado = -1;
        etNombre.setText("");
        etNombre.setError(null);
        etEdad.setText("");
        spEspecie.setSelection(0);
        rbMacho.setChecked(true);
        cbVacunado.setChecked(false);
        cbEsterilizado.setChecked(false);
        tvModo.setText("Nueva mascota");
        btnGuardar.setEnabled(true);
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }

    private int posicionDe(String especie) {
        for (int i = 0; i < especies.length; i++) if (especies[i].equals(especie)) return i;
        return 0;
    }

    private int imagenDe(String especie) {
        return imagenes[posicionDe(especie)];
    }

    private void configurar(StringRequest peticion) {
        // 8 s de espera, 1 reintento
        peticion.setRetryPolicy(new DefaultRetryPolicy(8000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void errorConexion(Exception error) {
        toast("No se pudo conectar con " + BASE_URL
                + "\n¿Está encendido Apache/MySQL en XAMPP?\nDetalle: " + error);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
