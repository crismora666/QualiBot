<?php

$TOKEN = "xxx";
$TELEGRAM = "https://api.telegram.org:443/bot$TOKEN"; 
$users = array(
   "2034" => "Cristian",
   "986" => "Wilson",
   "102" => "Christian",
   "678" => "Santiago",
   "97" => "David",
   "99" => "Marco",
   "74" => "Jorge",
   "247" => "William",
   "348" => "Xavier",
   "2111" => "Jofre",
   "698" => "JoseLuis",
   "448" => "Katy",
   "622" => "Janine",
   "1957" => "Luis",
   "13" => "Giovani",
   "47296" => "Raul",
   "440" => "Luis",
   "102" => "Rafael",
   "588" => "Carlos",
   "741" => "Test"
);

if (checkToken()) 
{  
  $request = receiveRequest();
  $chatId = $request->message->chat->id;
  $text = $request->message->text;

  if(array_key_exists($chatId, $users))
  {
    $qbot_con = mysql_pconnect("localhost","root","enero2012");
    // indicar base de datos
    mysql_select_db("qbot",$qbot_con);
    // sentencia sql
    $qbot_query = "INSERT INTO `chats` (`chatid`,`text`) VALUES ('$chatId','$text');";
    // enviar sentencia
    if(!mysql_query($qbot_query, $qbot_con)){
      print "0";
    } else{
      print "1";
      if(strtolower($text).trim() == "hola")
      {
        sendMessage($chatId, "Hola " . $users[$chatId] . ", soy el Oncall Virtual, espera hasta un minuto hasta que se prenda la laptop");
      }
    }
    mysql_close($qbot_con);
  } else{
    $qbot_con = mysql_pconnect("localhost","root","enero2012");
    // indicar base de datos
    mysql_select_db("qbot",$qbot_con);
    // sentencia sql
    $qbot_query = "INSERT INTO `noautorizado` (`chatid`) VALUES ('$chatId');";
    // enviar sentencia
    if(!mysql_query($qbot_query, $qbot_con)){
      print "0";
    } else{
      print "1";
    }
    mysql_close($qbot_con);

  }
}

function checkToken() 
{
  global $TOKEN;
  $pathInfo = ltrim($_SERVER['PATH_INFO'],'/');
  //echo $pathInfo;
  return $pathInfo == $TOKEN;
}

function receiveRequest() 
{
  $json = file_get_contents("php://input");
  $request = json_decode($json, $assoc=false);
  return $request;
}

function sendMessage($chatId, $text) 
{
  global $TELEGRAM;	

  $query = http_build_query([
    'chat_id'=> $chatId,
    'text'=> $text,
    'parse_mode'=> "Markdown", 
  ]);

  $response = file_get_contents("$TELEGRAM/sendMessage?$query");
  return $response;
}
?> 