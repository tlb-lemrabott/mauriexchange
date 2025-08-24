# MySQL Server Installation Guide:

### Complete guide to install my SQL on MacOS via Homebrew (Recommended)

- Install MySQL
``` bash
brew install mysql
```

- Start MySQL service
``` bash
brew services start mysql
```

- Stop MySQL service
``` bash
brew services stop mysql
```

Check MySQL version
``` bash
mysql --version
```

- Secure MySQL installation (set root password, remove test DB, etc.)
``` bash
mysql_secure_installation
```

- Login to MySQL
``` bash
mysql -u root -p
```