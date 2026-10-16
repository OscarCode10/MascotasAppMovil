<?php
// conexion.php — Conexión a MySQL (equivale al DatabaseConfig.php de la guía)
header('Content-Type: application/json; charset=utf-8');

$host    = "localhost";
$usuario = "umb_movil";     // usuario creado en mascotas_db.sql
$clave   = "movil2026";
$bd      = "mascotas_db";

mysqli_report(MYSQLI_REPORT_OFF);
$conn = new mysqli($host, $usuario, $clave, $bd);

if ($conn->connect_error) {
    echo json_encode(["ok" => false, "mensaje" => "Error de conexión: " . $conn->connect_error], JSON_UNESCAPED_UNICODE);
    exit;
}
$conn->set_charset("utf8mb4");

// Respuesta JSON uniforme para todos los servicios
function responder($ok, $mensaje, $data = null) {
    $r = ["ok" => $ok, "mensaje" => $mensaje];
    if ($data !== null) $r["data"] = $data;
    echo json_encode($r, JSON_UNESCAPED_UNICODE);
    exit;
}
