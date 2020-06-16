# Introduction

Redstone Revival is a lightweight plugin that lets players access powerful automation functions using a redstone block and a sign. No more will redstone be considered worthless. In fact, most will likely find it to be more valuable than diamonds with this plugin installed!

https://www.youtube.com/watch?v=o0uGqCjKstE

# Technical Description

Redstone Revival introduces several simple 'machines' to the game.

To build: place a redstone block down and attach a sign to it with some details describing the machine you want to create. The format is:

[MACHINE_NAME] <- Name of machine to make.

S=[Size] <- Size can be from 1 to 30 (configurable)

D=[Duration] <- Duration can be from 1 to 300 (configurable). Duration 0 machines operate instantly then break.

The name field has to be at the top, requires brackets and is required. The size and duration tags are always optional and can be in any order.

There are currently four machines in Redstone Revival. They are the [Funnel], [Breaker], [Miner] and [FLOOR_LAYER] machines. Thus, you could literally write [Miner] on a sign attached to a Redstone Block and, huzzah! You made a miner machine. So easy! (At least if you are a server OP. If not, then you will need to setup permissions.)

# The Machines

MINER -> Breaks blocks 'naturally' in an area around itself.
- If size 1, it will run once per second. If a larger size, it will run once every five seconds. If its size is above 10 (configurable), then it will only run once.
- It will not mine certain restricted materials (water, lava, bedrock, oak_wall_sign, redstone_block, air by default (configurable))
- Mining 'size' parameter cap of 30 (configurable). To be clear, s=30 means a 60x60x60 cube will get cleared. THIS IS HUGE.
- It accepts size and duration arguments.

BREAKER -> Breaks a block on 'the other side' of the Redstone block opposite of the wall_sign that you have attached. It is a very lightweight alternative to the AOE miner.
- It will not break certain restricted materials (water, lava, bedrock, oak_wall_sign, redstone_block, air by default (configurable))
- It accepts a duration argument.

FUNNEL -> Funnel sucks up items in a 4x4x4 radius centered on the redstone block of the machine and deposits them into a nearby chest.
- The funnel 'runs' once every five seconds.
- The chest needs to be within 1 block of the redstone block for this machine to build.
- This machine accepts a duration argument.

FLOOR_LAYER -> This machine places blocks from a specifically placed chest onto the ground underneath the redstone block.
- The chest needs to be directly underneath the redstone block.
- It 'lays' blocks down in a spiral pattern extending outward in a square.
- Activates instantly when pressing sign and will only use available blocks.
- Does not support laying multiple types of materials. (Use two machines for two different blocks)
- Will not place blocks on anything except air.
- Does not accept any arguments, as it is instant and uses resources up to configuration value (FLOOR_LAYER_MAX_ATTEMPTS). (see below for info)

# Additional Details & Notes

Because Redstone Revival is so simple, you really don't have to do anything to manage it. It manages itself. For example, all machines expire after a set amount of time and players have to create new machines if redstone consumption (the config option) is enabled or restart the machine if it isn't.

With that said, you might not want players making hundreds of size 30 machines on your server, so I've added permissions and configuration options to let server owners control how the machines behave. By default, players can't make any machine until you give them permission to use at least one machine. Operators have full privledges by default.

# Permissions

RedstoneRevival.* = Admin privledge. Unlimited max machine properties up to config file caps. Can build all machines.

RedstoneRevival.max_size.10 <- From 1 to infinity.

RedstoneRevival.max_duration.300 <- Has to be a multiple of 5. (0, 5, 10, 15, 20, ... 1000)

RedstoneRevival.use_machine.* <- Permission to use all machines.

RedstoneRevival.use_machine.floor_layer <- Permission to use [FLOOR_LAYER] machine.

RedstoneRevival.use_machine.funnel

RedstoneRevival.use_machine.breaker

RedstoneRevival.use_machine.miner

RedstoneRevival.max_machines.10 <- Number from 1 to 100.

# Config Settings

'Default' prefixed config options provide values for unset permisisons aren't set. Global config options set hard limits on permissions.

Please note that anything prefixed by 'default' will not override permissions and anything prefixed by 'global' WILL override permissions.

\#The format of the config file is states a the default value of the config option and its description prefixed by #, followed by what it would actually look like in the config file.

\#Default Value / Description

\#\[property\]\: \[value\]

\#256 / Max attempts that a floor layer will make to place a block. I recommend adjusting to your servers specs.

floor_layer_max_attempts: 256

\#It's strongly recommended to keep the defaults. Add more blocks at will.

restricted_break_list:
- WATER
- LAVA
- BEDROCK
- OAK_WALL_SIGN
- REDSTONE_BLOCK
- AIR
- BEDROCK

\#Whether redstone is consumed when blocks end or not.

destroy_machines_on_end: true

\#Default max size that machines can run for when they have use_machine and no custom max_size permission.

default_max_size: 10

\#Default max duration that machines can run for when they have use_machine and no custom max_duration permission.

default_max_duration: 300

\#Default max number of machines users can have on server

default_max_machines: 10

\#The absolute max duration for any machine.

global_max_duration: 1800

\#Strongly recommend not increasing this value as, even if your server can handle this, it can cause lag and timeout users whose clients can't handle large world changes. (For the [Miner] block, think 'Massive TNT explosions' if you set this high.) (Though, honestly running a size '50' miner is pretty satisfying to watch! ;o)

global_max_size: 30

\#Hard cap on max machines that each user can have on the server.

global_max_machines: 50

\#Hard cap on total machines that can run on a server.

global_server_max_machines: 500

\#The size cutoff before miners will no longer operate repeatedly in a loop and will only run once before breaking.

miner_loop_size_cutoff: 10

\#Optional setting to make redstone_ore drop only one redstone. Even though the default value is false, I strongly recommend setting it to true your server can support it without conflicts.

redstone_ore_drops_one: false

# A Word of Warning

As a small note with this plugin, be careful with having multiple machine parts next to each other when activating a machine (especially redstone_blocks). Nothing should ever break or work 'wrong'. However, players might see unexpected behavior, as the search methods for nearby blocks operate on a 'first come first serve' basis. So, if you have three redstone blocks next to a sign, it will pick the first one that it 'finds', not necessarily the block that it is attached to.

# Conclusion

If you have any feedback on this plugin, please share. This plugin is fully open-source and anybody can expand on it, just attribute credit please. Thanks!

Github: https://github.com/Destro168/RedstoneRevival

I sometimes stream my coding on twitch: https://www.twitch.tv/destrothegod

Enjoy!