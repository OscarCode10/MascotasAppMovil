<?php
// insertar.php — CREATE: recibe los datos por POST (Volley StringRequest)
require 'conexion.php';

$nombre       = trim($_POST['nombre'] ?? '');
$especie      = $_POST['especie'] ?? '';
$sexo         = $_POST['sexo'] ?? '';
$edad         = intval($_POST['edad'] ?? 0);
$vacunado     = intval($_POST['vacunado'] ?? 0);
$esterilizado = intval($_POST['esterilizado'] ?? 0);

if ($nombre === '' || $especie === '' || $sexo === '') responder(false, "Faltan datos obligatorios");

$stmt = $conn->prepare("INSERT INTO mascotas (nombre, especie, sexo, edad, vacunado, esterilizado) VALUES (?, ?, ?, ?, ?, ?)");
$stmt->bind_param("sssiii", $nombre, $especie, $sexo, $edad, $vacunado, $esterilizado);

if ($stmt->execute()) responder(true, "Mascota registrada (id " . $stmt->insert_id . ")");
responder(false, "Error al registrar: " . $stmt->error);
