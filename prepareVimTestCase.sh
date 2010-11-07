maj=7
min=3
wget ftp://ftp.vim.org/pub/vim/unix/vim-$maj.$min.tar.bz2
tar xjf vim-$maj.$min.tar.bz2
cd vim$maj$min

# Compilation options used for Vim. FEAT_TINY makes sure most features are left
# variable.
./configure --enable-pythoninterp --with-features=tiny
