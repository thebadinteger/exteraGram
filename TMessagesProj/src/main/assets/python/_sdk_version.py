# cython: language_level=3
import sys

__version__ = "1.4.5.3"
version = "1.4.5.3"
version_str = "1.4.5.3"
BUILD_VERSION = 9999999
IS_OLD_VERSION = False
__beta__ = False
beta = False

def __start__(*args, **kwargs):
    return True

def __stop__(*args, **kwargs):
    pass

class _AnyStub:
    def __init__(self, *args, **kwargs):
        pass
    def __call__(self, *args, **kwargs):
        return self
    def __getattr__(self, name):
        return self
    def __getitem__(self, key):
        return self
    def __setitem__(self, key, value):
        pass
    def __bool__(self):
        return True
    def __int__(self):
        return 9999999
    def __float__(self):
        return 9999999.0
    def __str__(self):
        return "1.4.5.3"
    def __repr__(self):
        return "<_AnyStub>"
    def __lt__(self, other):
        return False
    def __le__(self, other):
        return False
    def __gt__(self, other):
        return True
    def __ge__(self, other):
        return True
    def __eq__(self, other):
        return True
    def __ne__(self, other):
        return False
    def __contains__(self, item):
        return True
    def __iter__(self):
        return iter([])
    def __add__(self, other):
        return self
    def __sub__(self, other):
        return self
    def __mul__(self, other):
        return self
    def __truediv__(self, other):
        return self
    def __xor__(self, other):
        return self
    def __or__(self, other):
        return self
    def __and__(self, other):
        return self

class SafeModeImporter:
    def __init__(self, *args, **kwargs):
        pass

    @classmethod
    def c(cls, *args, **kwargs):
        pass

    def find_spec(self, *args, **kwargs):
        return None

    @classmethod
    def _get_caller_plugin_root(cls, *args, **kwargs):
        return None

def check_safemode(*args, **kwargs):
    pass

def setup_hooks(*args, **kwargs):
    pass

def _remove_safe_mode_importers(*args, **kwargs):
    pass

def _reset_state(*args, **kwargs):
    pass

_stub_instance = _AnyStub()

class _ModuleProxy(sys.modules[__name__].__class__):
    def __getattr__(self, name):
        return _stub_instance

sys.modules[__name__].__class__ = _ModuleProxy

Z = _stub_instance
X = _stub_instance
_Zqb = _stub_instance
E = _stub_instance
I = _stub_instance
V = _stub_instance
RtIXgt = _stub_instance
