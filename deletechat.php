<?php

  $fila = isset($_GET['fila']) ? $_GET['fila'] : '0';

  //indicar servidor
  $qbot_con = mysql_pconnect("localhost","root","enero2012");
  // indicar base de datos
  mysql_select_db("qbot",$qbot_con);
  // sentencia sql
  $qbot_query = "delete from `chats` where id = $fila;";
  // enviar sentencia
  if(mysql_query($qbot_query, $qbot_con)){
    print "1";
  }else{
    print "0";
  }
  mysql_close($qbot_con);
?> 