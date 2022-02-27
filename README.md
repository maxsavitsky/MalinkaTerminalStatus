# MalinkaTerminalStatus

This program displays some system information in terminal

Terminal separated into 2 sections: system status section and messages section.

Example:
![image](https://user-images.githubusercontent.com/38137967/155898556-34c6aaa8-1257-4a0a-a0e4-c2129dcb44bd.png)

On Linux you can also specify services which status will be displayed (see help for detailes):
![image](https://user-images.githubusercontent.com/38137967/155898532-822838e5-1dd7-4db5-a211-476bf58f7b86.png)

## Run
```console
$ git clone https://github.com/maxsavitsky/MalinkaTerminalStatus.git
$ cd MalinkaTerminalStatus
$ mvn package
$ java -jar target\MalinkaTerminalStatus-1.0-shaded.jar
```

## Help
You can also see this output if you specify `help` as the first parameter
```
ESC to exit

Arguments:
        --tty=PATH_TO_TTY_DEV_BLOCK
                Specifies where terminal should be displayed.
                Input and output streams will be redirected.
                For example, --tty=dev/tty4
                Default is current terminal

        --disable-temp-control
                Disables temperature control (notifications, emergency shutdown).

        --execute-after-startup=COMMAND
                This command will be executed after terminal will be ready to display data.

        --port=PORT
                Specifies port which socket will listen.
                Default is 8000

        --services-list=path_to_file
                This file describes the services whose status should be displayed.
                Each service is described by a separate line in the format id:name
                id should be identifier of service in systemctl
                Note: Works only on linux
```

## About name
'Malinka' is form of russian word 'malina' which means 'raspberry'
