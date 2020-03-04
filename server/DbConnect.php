<?php
    include_once "config.php";   
    
    class DbConnect{
        private $conn;
       	 
        function __construct(){

        }
        
        public function connect(){
            $this->conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);

            if(mysqli_connect_error()){
                echo "Database connection failed".mysqli_connect_error();
            }

            return $this->conn;
            }
    }
?>
