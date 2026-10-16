<?php
// actualizar.php — UPDATE: modifica la mascota con el id recibido
require 'conexion.php';

$id           = intval($_POST['id'] ?? 0);
$nombre       = trim($_POST['nombre'] ?? '');
$especie      = $_POST['especie'] ?? '';
$sexo         = $_POST['sexo'] ?? '';
$edad         = intval($_POST['edad'] ?? 0);
$vacunado     = intval($_POST['vacunado'] ?? 0);
$esterilizado = intval($_POST['esterilizado'] ?? 0);

if ($id <= 0) responder(false, "Id inválido");
if ($nombre === '' || $especie === '' || $sexo === '') responder(false, "Faltan datos obligatorios");

$stmt = $conn->prepare("UPDATE mascotas SET nombre = ?, especie = ?, sexo = ?, edad = ?, vacunado = ?, esterilizado = ? WHERE id = ?");
$stmt->bind_param("sssiiii", $nombre, $especie, $sexo, $edad, $vacunado, $esterilizado, $id);

if (!$stmt->execute()) responder(false, "Error al actualizar: " . $stmt->error);
if ($stmt->affected_rows === 0) responder(true, "Sin cambios (los datos eran iguales o el id no existe)");
responder(true, "Mascota actualizada");
