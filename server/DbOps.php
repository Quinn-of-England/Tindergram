<?php
    include_once "DbConnect.php";
    
    class DbOps{
        private $conn;

        function __construct(){
            $db = new DbConnect();

            $this->conn = $db->connect();
        }

        public function createUser($username, $password, $email){
            if($this->isUserExist($username, $email)) {
                return 0;
            }else{
                $encrPassword = md5($password);
                $stmt = $this->conn->prepare("INSERT INTO `users`(`username`, `email`, `password`, `following`) VALUES (?,?,?,NULL);");
                $stmt->bind_param("sss", $username, $email, $encrPassword);

                if($stmt->execute()){
                    return 1;
                }else{
                    echo $this->conn->error;    
                    return 2;
                }
            }
        }

        private function isUserExist($username, $email) {
            $stmt = $this->conn->prepare("SELECT id FROM users WHERE username = ? OR email = ?");
            $stmt->bind_param("ss", $username, $email);
            $stmt->execute(); 
            $stmt->store_result(); 
            return $stmt->num_rows > 0; 
        }

        public function loginUser($username, $password) {
            $encrPassword = md5($password);
            $stmt = $this->conn->prepare("SELECT id FROM users WHERE username = ? AND password = ?");
            $stmt->bind_param("ss", $username, $encrPassword);
            $stmt->execute();
            $stmt->store_result(); 
            return $stmt->num_rows > 0; 
        }

        public function getUserByUsername($username) {
            $stmt = $this->conn->prepare("SELECT * FROM users WHERE username = ?");
            $stmt->bind_param("s", $username);
            $stmt->execute();
            return $stmt->get_result()->fetch_assoc();
        }
    }
?>
