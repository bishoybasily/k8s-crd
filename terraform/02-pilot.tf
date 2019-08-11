resource "aws_instance" "vm-pilot" {
  tags {
    Name = "pilot"
  }
  ami = "ami-06358f49b5839867c"
  instance_type = "t2.micro"
  key_name = "${var.key-pair-name}"
  security_groups = [
    "${var.security-group}"
  ]
}