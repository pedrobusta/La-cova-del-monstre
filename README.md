# La cova del monstre
Construye un programa a partir de la versión simplificada del problema de la cueva del monstruo para que el agente alcance el tesoro y, una vez alcanzado, salga de la cueva con el mismo.
> Entrega semana del 20 de noviembre
### Requisitos mínimos de la pràctica (15 puntos)
* La cueva se puede implementar como un tablero de nXn cuadros, siendo n variable. El usuario seleccionará el valor de n para cada ejecución del programa.
* En la cueva existe un monstruo y tantos precipicios como se desee. Hay un único tesoro.
* El usuario debe situar el monstruo, el tesoro y los precipicios en las posiciones de la cueva que desee.
* El agente siempre accede y sale por la misma casilla [1,1] en la cueva. Esta casilla no puede contener monstruos ni precipicios.
* El agente puede moverse según las reglas descritas en la exposición del tema en clase: NORTE, SUR, ESTE, OESTE.
* Una vez encontrado el tesoro, el agente debe salir de la cueva con el mismo, por la casilla donde entró [1. 1].
* El agente nunca puede perecer en el intento de encontrar el tesoro. En caso de que el tesoro sea inalcanzable por el agente, éste continuará buscándolo indefinidamente
### Requisitos opcionales (2.5 puntos/requisito hasta un máximo de 25 puntos)
1. Pueden existir varios monstruos en la cueva. Cada monstruo se situará en las posición donde decida el usuario.
2. El agente puede matar los monstruos. Para ello dispone de tantas flechas como monstruos existan en la cueva. Las flechas sólo pueden ser lanzadas en línea recta, en una fila o columna. Si un monstruo muere, el ambiente debe actualizar
las percepciones que envia al agente con objeto de que los correspondientes cuadros no supongan un peligro.
3. Pueden existir varios agentes en busca del tesoro, de manera que cada agente viene adecuadamente identificado. Cada agente entra en la cueva por una casilla diferente. Se trata de observar cual de los agentes alcanzará el tesoro y saldrá por su correspondiente entrada primero. Éste será el agente ganador. El alumno puede, opcionalmente, introducir elementos de competencia entre los agentes, por ejemplo, la posibilidad de que un agente pueda producir un hedor característico durante algunos instantes, que confunda a los otros agentes, para que éstos piensen que existe un monstruo en las casillas colindantes a aquella donde se produce este hedor. Puede considerarse también que, al ser inteligentes los agentes, entonces éstos no se verán confundidos si el hedor simulado por otro agente se produce en una casilla que ya ha sido visitada y etiquetada como segura.
4. Pueden existir varios agentes y varios tesoros en el ambiente, de manera que el agente que alcance más tesoros es el vencedor.
### Consideraciones sobre la pràctica
* La práctica debe contener una interface gráfica de usuario donde se representen, mediante una cuadrícula que ilustra el entorno, los elementos del entorno del problema y donde se ilustre el recorrido del agente a través del ambiente.
* El usuario será quien sitúe los precipicios, el monstruo y el/los tesoro/s inicialmente en el entorno.
* El recorrido del agente debe realizarse a una velocidad adecuada que permita la observación del funcionamiento del mismo. Se aconseja usar dos –o más- velocidades del agente en su recorrido, una de las cuales debe ser manual, para que el usuario compruebe el buen funcionamiento del agente.
* La puntuación de la práctica es de 15 puntos si se implementan solamente los requisitos mínimos.
* Además de los requisitos mínimos pueden implementarse requisitos opcionales, la bonificación extra es de 2.5 puntos por requisito opcional. Es decir, la pràctica puntúa un máximo de 25 puntos.

