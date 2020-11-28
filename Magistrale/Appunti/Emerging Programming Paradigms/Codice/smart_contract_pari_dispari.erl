-module(even_odd).
-export([test/0]).

% Questo pseudo-codice Erlang mostra le problematiche legate
% alla scrittura di codice la cui correttezza consiste nel verificare che
% se tutti gli utenti di uno smart contract si comportano egoisticamente
% (massimizzando il proprio guadagno), allora il comportamento globale
% è quello voluto.
%
% Il file implementa un gioco dove:
% - due giocatori devono scegliere un numero
% - se la somma è pari il primo giocatore vince 1 coin e il secondo ne perde 1
% - altrimenti accade il contrario
%
% Problematiche:
% 1) bisogna impedire che il perdente non paghi
%    ==> farlo pagare in anticipo quando si iscrive per giocare
% 2) impedire che un partecipante veda la puntata dell'altro
%    ==> lo stato di uno smart contract è pubblico; pertanto i giocatori
%        devono comunicare la giocata criptata e in una seconda fase
%        comunicarla nuovamente non criptata; lo smart contract userà la
%        chiave del giocatore per verificare che chi rivela non cambi il
%        numero
% 3) impediare al perdente di non rivelare la sua giocata quando sa di aver
%    perso per non danneggiare il vincitore
%    ==> prelevare dai giocatori non 1, ma 2 coin e restituirne una al perdene

% GiocatoreX è il PID del giocatore, none se sconosciuto
% CriptNumeroX è la giocata criptata di X, none se non ancora effettuata
% NumeroX è la giocata in chiaro di X, none se non ancora effettuata
loop(Giocatore1,Giocatore2,CriptNumero1,CriptNumero2,Numero1,Numero2) ->
 % prima verifico se la partita è terminata
 case {Numero1, Numero2 } of
    { none, _ } -> ok ;
    { _, none } -> ok ;
    _ ->
     case (Numero1 + Numero2) rem 2 =:= 0 of
      true ->
       Giocatore1 ! three_bitcoin, % qua si intende trasferimento reale di criptovaluta
       Giocatore2 ! one_bitcoin,
       loop(none,none,none,none,none,none) ;
      false ->
       Giocatore2 ! three_bitcoin,
       Giocatore1 ! one_bitcoin,
       loop(none,none,none,none,none,none)
     end
 end,
 receive
   % messaggio per partecipare al gioco
   % la ricezione di two_bitcoin va intesa come trasferimento reale di criptovaluta
   {partecipa, Pid, two_bitcoin} ->
     case { Giocatore1, Giocatore2 } of
        { none, _ } -> loop(Pid,Giocatore2,CriptNumero1,CriptNumero2,Numero1,Numero2) ;
        { _, none } -> loop(Giocatore1,Pid,CriptNumero1,CriptNumero2,Numero1,Numero2) ;
        _ -> loop(Giocatore1,Giocatore2,CriptNumero1,CriptNumero2,Numero1,Numero2)
     end ;
   % messaggio per puntare
   % Il numero è criptato con la chiave pubblica del giocatore
   {gioca,Pid,CriptNumero} ->
     case Giocatore1 =:= Pid andalso CriptNumero1 =:= none of
        true -> loop(Giocatore1,Giocatore2,CriptNumero,CriptNumero2,Numero1,Numero2) ;
        false ->
          case Giocatore2 =:= Pid andalso CriptNumero1 =:= none of
           true -> loop(Giocatore1,Giocatore2,CriptNumero1,CriptNumero,Numero1,Numero2) ;
           false -> loop(Giocatore1,Giocatore2,CriptNumero1,CriptNumero2,Numero1,Numero2)
          end
     end ;
   % messaggio per rivelare
   % Viene rivelata la giocata
   {rivela,Pid,Numero} ->
     case CriptNumero1 =/= none andalso CriptNumero2 =/= none of
      true ->
       case Giocatore1 =:= Pid andalso decripta(CriptNumero1) =:= Numero of
        true -> loop(Giocatore1,Giocatore2,CriptNumero,CriptNumero2,Numero,Numero2) ;
        false ->
         case Giocatore2 =:= Pid andalso decripta(CriptNumero2) =:= Numero of
          true -> loop(Giocatore1,Giocatore2,CriptNumero,CriptNumero2,Numero1,Numero) ;
          false -> loop(Giocatore1,Giocatore2,CriptNumero,CriptNumero2,Numero1,Numero2)
         end
       end ;
     false ->  loop(Giocatore1,Giocatore2,CriptNumero,CriptNumero2,Numero1,Numero2)
    end
end.

test() ->
 loop(none,none,none,none,none,none).

