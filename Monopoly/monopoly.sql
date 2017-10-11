DROP database if exists Monopoly;
CREATE database Monopoly;
USE Monopoly;
create table userData(
	musername varchar(50) primary key not null,
    mpassword varchar(50) not null,
    mwins int(10) not null,
    mgameplayes int(10) not null
    
);