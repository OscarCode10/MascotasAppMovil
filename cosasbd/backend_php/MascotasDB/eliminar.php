<?php
// eliminar.php — DELETE: borra la mascota con el id recibido
require 'conexion.php';

$id = intval($_POST['id'] ?? 0);
if ($id <= 0) responder(false, "Id inválido");

$stmt = $conn->prepare("DELETE FROM mascotas WHERE id = ?");
$stmt->bind_param("i", $id);

if (!$stmt->execute()) responder(false, "Error al eliminar: " . $stmt->error);
if ($stmt->affected_rows === 0) responder(false, "No existe una mascota con id $id");
responder(true, "Mascota eliminada");
