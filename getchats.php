<?php

  //indicar servidor
  $qbot_con = mysql_pconnect("localhost","root","enero2012");
  // indicar base de datos
  mysql_select_db("qbot",$qbot_con);
  // sentencia sql
  $qbot_query = "select chatid,text,id from chats order by id;";
  // enviar sentencia
  $qbot_datos = mysql_query($qbot_query, $qbot_con);
  // recuperar datos
  $i_bot = 0;
  while($qbot_fila = mysql_fetch_assoc($qbot_datos)) {  
    if($i_bot > 0) print "\n"; 	
    print $qbot_fila['chatid'] . ";" . $qbot_fila['text'] . ";" . $qbot_fila['id'];
    $i_bot = $i_bot + 1;
  }
  mysql_free_result($qbot_datos);
  mysql_close($qbot_con);
?> 