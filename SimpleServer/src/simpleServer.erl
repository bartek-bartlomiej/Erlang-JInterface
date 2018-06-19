-module(simpleServer).
-author("bsps").

%% API
-export([start_link/0, init/0]).

start_link() ->
  register(sServerMailbox, spawn_link(simpleServer, init, [])).

init() ->
  rand:seed(exs1024s, {erlang:phash2(["Secret of MI"]), erlang:monotonic_time(), erlang:unique_integer()}),
  loop().

loop() ->
  receive
    {request, Pid, atom} ->
      Pid ! secretAtom,
      loop();

    {request, Pid, number} ->
      Pid ! rand:uniform(10000000),
      loop();

    {request, Pid, fraction} ->
      Pid ! rand:uniform(100000) / rand:uniform(100),
      loop();

    {request, Pid, tuple} ->
      Pid ! {ok, 4, [a, b, c]},
      loop();

    {request, Pid, map} ->
      Pid ! #{carrot => 4, pepper => 3},
      loop();

    {request, Pid, pid} ->
      Pid ! self(),
      loop();

    {request, Pid, list} ->
      Pid ! [1104, 1108, 1105, 1116, 1123, 1142],
      loop();

    {request, Pid, stop} ->
      Pid ! {reply, ok}

  end.