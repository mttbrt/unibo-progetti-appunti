-module(hot_swap2).
-export([main/0,loop/1,new_loop/1]).

% Uso:
% c(hot_swap2).
% hot_swap2:main().

% Iniziare scommentando questa implementazione che
% passa due variabili in giro e usa i messaggi add1 e add2.
%loop(N,M) ->
%  io:format("N=~p, M=~p~n",[N,M]),
%  receive
%    {add1, X} -> loop(N+X,M) ; % queste chiamate sono al codice ora in uso
%    {add2, X} -> loop(N,M+X) ;
%    update -> % il mssaggio update serve per iniziare lo switch al nuovo codice 
%              % prima di inviare update compilare la nuova versione del codice
%              % in modo che la BEAM le carichi entrambe in memoria
%      io:format("Update to new code~n",[]),
%      hot_swap:new_loop(N,M) % la sintassi MODULO:FUNZIONE invoca la versione più
%                             % recente di una funzione e non quella ora in uso
%  end.

% Poi scommentare questa che passa in giro un record.
% Questa versione capisce sia il vecchio che il nuovo formato dei messaggi.
%loop(R = {N,M}) ->
%  io:format("~p~n",[R]),
%  receive
%    {add1, X} -> loop({N+X,M}) ;
%    {add2, X} -> loop({N,M+X}) ;
%    {add, 1, X} -> loop({N+X,M}) ;
%    {add, 2, X} -> loop({N,M+X}) ;
%   update ->
%    io:format("Update to new code~n",[]),
%    hot_swap:new_loop({N,M})
% end.

% Poi scommentare questa che passa in giro un record e capisce solo il
% nuovo formato.
%loop(R = {N,M}) ->
%  io:format("~p~n",[R]),
%  receive
%    {add, 1, X} -> loop({N+X,M}) ;
%    {add, 2, X} -> loop({N,M+X}) ;
%   update ->
%    hot_swap:new_loop({N,M})
% end.

% Prima versione, nessun update
%new_loop(N,M) ->
% loop(N,M).

% Seconda versione, cambio di struttura dati
%new_loop(N,M) ->
%  loop({N,M}).

% Terza versione, nessun update
%new_loop(R) ->
% loop(R).

main() ->
  spawn(?MODULE,loop,[0,0]).
