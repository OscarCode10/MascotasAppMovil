<?php
// listar.php — READ: devuelve todas las mascotas (se puede probar en el navegador)
require 'conexion.php';

$res = $conn->query("SELECT id, nombre, especie, sexo, edad, vacunado, esterilizado FROM mascotas ORDER BY id DESC");
if (!$res) responder(false, "Error: " . $conn->error);

$lista = [];
while ($f = $res->fetch_assoc()) {
    $f["id"]           = (int)$f["id"];
    $f["edad"]         = (int)$f["edad"];
    $f["vacunado"]     = (int)$f["vacunado"];
    $f["esterilizado"] = (int)$f["esterilizado"];
    $lista[] = $f;
}
responder(true, count($lista) . " mascota(s) encontradas", $lista);
