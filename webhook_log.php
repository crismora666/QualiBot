<?php

$request = file_get_contents("php://input");

$date = date('Y-m-d H:i:s');

$content = "$date\n$request\n\n";

file_put_contents("webhook.log", $content, FILE_APPEND);?> 