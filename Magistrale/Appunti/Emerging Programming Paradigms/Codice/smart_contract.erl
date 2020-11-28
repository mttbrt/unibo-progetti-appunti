-module(smart).
-export([intermediario/5]).

% Questo è un esempio di smart contract scritto in Erlang.
% Nota: che io sappia non esiste nessun compilatore da Erlang a bytecode
% di una qualunue blockchain. Tuttavia il codice che scrivereste per esempio
% in Liquidity (il linguaggio di smart contracts di Tezos) sarebbe quasi
% identico, a meno delle differenze sintattiche (Liquidity è un subset di
% OCaml e ogni receive branch sarebbe una funzione invocabile).

% intermediario() gestisce compra-vendite di beni dove il compratore effettua
% una sequenza di pagamenti prima di raggiungere il valore del bene.
%
% Il compratore V, per vendere un bene B a un prezzo P, esegue
%    intermediario!{offerta, V, B, P}
% Supponiamo che il bene venga immediatamente trasferito all'intermediario.
%
% Il compratore A effettua ogni versamento di S soldi tramite
%    intermediario!{pagamento, A, S}
%
% Al raggiungimento della cifra pattuita il venditore riceve i soldi tramite
%    venditore!NuoviSoldi
% e l'acquirente il bene tramite
%    acquirente!Bene
%
% Venditori e acquirenti si fidano del codice in quanto il codice è reso
% pubblico e immutabile mettendolo sulla blockchain.
%
% Invocare la prima volta con (none,none,none,0,none)
intermediario(Bene,Venditore,Prezzo,Soldi,Acquirente) ->
 receive
   {offerta, V, B, P} -> intermediario(B,V,P,0,none) ;
   {pagamento, A, S} ->
     AcqOk =
      case Acquirente of
         none -> true ;
         A ->  true ;
         _ -> false
      end,
    case AcqOk of
      true ->
         NuoviSoldi = Soldi + S,
         case NuoviSoldi > Prezzo of
            true ->
              A ! Bene,
              Venditore ! NuoviSoldi,
              intermediario(none,none,none,0,none) ;
            false ->
              intermediario(Bene,Venditore,Prezzo,NuoviSoldi,A)
         end ;
     false ->
        intermediario(Bene,Venditore,Prezzo,Soldi,Acquirente)
    end
 end.

